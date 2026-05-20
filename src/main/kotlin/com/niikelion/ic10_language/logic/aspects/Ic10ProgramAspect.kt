package com.niikelion.ic10_language.logic.aspects

import com.intellij.openapi.Disposable
import com.intellij.ui.components.JBTextField
import com.niikelion.ic10_language.logic.*
import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder
import com.niikelion.ic10_language.logic.state.*
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import com.niikelion.ic10_language.ui.swing.fillWidth
import com.niikelion.ic10_language.ui.swing.jb.label
import com.niikelion.ic10_language.ui.swing.jb.titledFoldout
import com.niikelion.ic10_language.ui.swing.layout.*
import com.niikelion.ic10_language.ui.swing.maxWidth
import com.niikelion.ic10_language.ui.swing.state.coroutineScope
import com.niikelion.ic10_language.utils.alsoLet
import com.niikelion.ic10_language.utils.toPrettyString
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.Dimension
import javax.swing.JComponent
import kotlin.math.max
import kotlin.reflect.KClass

class Ic10ProgramAspect(
    val code: ProgramCode,
    val lineNumberPropertyId: Int? = null,
    val errorPropertyId: Int? = null,
    val ownDeviceId: Long? = null
): DeviceAspect {
    companion object {
        const val TICK_PER_SECOND = 2
    }

    override val name = "Program Memory"
    override val stateClass: KClass<out DeviceAspect.State> = State::class

    override fun renderDebuggerView(flow: StateFlow<DeviceAspect.State>, scope: Disposable): JComponent = SwingBuilder().column({
        justify(Justification.FILL)
    }) {
        val initialValue = flow.value
        if (initialValue !is State) return@column

        row({ align(Alignment.CENTER); justify(Justification.FILL) }) {
            label("Sleeping for(ticks)")
            element {
                JBTextField(initialValue.waitingFor.toString()).also {
                    it.isEnabled = false
                    it.isEditable = false
                    it.maxWidth(200)
                    it.fillWidth()
                    flow.onEach { state ->
                        if (state !is State) return@onEach
                        it.text = state.waitingFor.toString()
                    }.launchIn(scope.coroutineScope())
                }
            }
        }
        titledFoldout("Registers") {
            wrap({ gap(4) }) {
                Registers.all.forEach { register ->
                    row({ align(Alignment.CENTER); justify(Justification.FILL) }) {
                        val registerAliasesPart = Registers
                            .aliasesFor(register)
                            .let { if (it.isEmpty()) "" else "(${it.joinToString(", ")})" }
                        label("${register.name}${registerAliasesPart}:")
                        element {
                            JBTextField((initialValue.registers[register] ?: 0.0).toPrettyString()).also {
                                it.isEditable = false
                                it.isEnabled = false
                                it.maxWidth(100)
                                it.fillWidth()
                                flow.onEach { state ->
                                    if (state !is State) return@onEach
                                    it.text = (state.registers[register] ?: 0.0).toPrettyString()
                                }.launchIn(scope.coroutineScope())
                            }
                        }
                    }.also {
                        it.maxWidth(160)
                        it.preferredSize = Dimension(160, it.preferredSize.height)
                    }
                }
            }
        }
    }
    override fun tick(state: SimulationStateChangeBuilder, deviceId: Long) {
        state.device(deviceId).alsoLet {
            aspect<State, State.Change.Builder, Unit> {
                repeat(128) {
                    if (waitingFor > 0) return@repeat

                    // sync internal state with device properties if present
                    lineNumberPropertyId?.also { jump(property(it).toInt()) }

                    if (isOnFire) return@aspect

                    val line = code.lines.elementAtOrNull(instructionIndex)
                    val action = line?.instruction?.action

                    try {
                        if (action != null) {
                            val args =
                                line.args.map { it.resolve(this) ?: throw Exception("Error resolving $it") }
                                    .toTypedArray()
                            InstructionContext(state, deviceId)
                                .action(args)
                        }
                        jump((instructionIndex + 1) % 128)
                    } catch (_: Throwable) {
                        lightOnFire()
                        //TODO: report error, maybe by callback?
                    }
                    // if present, sync device properties with internal state
                    errorPropertyId?.also { setProperty(it, if (isOnFire) 1.0 else 0.0) }
                    lineNumberPropertyId?.also { setProperty(it, instructionIndex.toDouble()) }
                }

                if (waitingFor > 0)
                    decrementWaitFor()
            }
        }
    }

    override fun initialize(): State {
        val initialDevices = DeviceSlots.all.associateWith { 0L }.toMutableMap()
        if (ownDeviceId != null) initialDevices[DeviceSlots.db] = ownDeviceId
        return State(devices = initialDevices)
    }

    class State(
        val registers: Map<Register, Double> = Registers.all.associateWith { 0.0 },
        val devices: Map<DeviceSlot, Long> = DeviceSlots.all.associateWith { 0 },
        val onFire: Boolean = false,
        val instructionIndex: Int = 1,
        val waitingFor: Int = 0
    ): DeviceAspect.State {
        override fun change(): Change.Builder = Change.Builder(this)

        class Change(
            val registers: Map<Register, SimpleChange<Double>> = emptyMap(),
            val devices: Map<DeviceSlot, SimpleChange<Long>> = emptyMap(),
            val onFire: SimpleChange<Boolean>? = null,
            val instructionIndex: SimpleChange<Int>? = null,
            val waitingFor: SimpleChange<Int>? = null
        ): DeviceAspect.State.Change, CompositeChange<DeviceAspect.State> {
            override fun compose(source: DeviceAspect.State, action: CompositeChangeAction): DeviceAspect.State {
                if (source !is State) return source // passthrough if target has invalid type

                return action.compose {
                    State(
                        compose(source.registers, registers),
                        compose(source.devices, devices),
                        compose(source.onFire, onFire),
                        compose(source.instructionIndex, instructionIndex),
                        compose(source.waitingFor, waitingFor)
                    )
                }
            }

            class Builder(
                private val previousState: State
            ): DeviceAspect.State.Change.Builder, IProgramState {
                private val registers = mutableMapOf<Register, SimpleChange<Double>>()
                private val devices = mutableMapOf<DeviceSlot, SimpleChange<Long>>()
                private var onFire: SimpleChange<Boolean>? = null
                private var instructionIndexValue: SimpleChange<Int>? = null
                private var waitingForValue: SimpleChange<Int>? = null

                override fun get(register: Register): Double = registers[register]?.nextValue ?: previousState.registers[register] ?: 0.0
                fun get(deviceSlot: DeviceSlot): Long = devices[deviceSlot]?.nextValue ?: previousState.devices[deviceSlot] ?: 0
                val isOnFire get() = onFire?.nextValue ?: previousState.onFire
                val instructionIndex get() = instructionIndexValue?.nextValue ?: previousState.instructionIndex

                val waitingFor get() = waitingForValue?.nextValue ?: previousState.waitingFor

                fun set(register: Register, value: Double) {
                    val previous = previousState.registers[register] ?: return
                    registers[register] = SimpleChange(previous, value)
                }
                fun lightOnFire() {
                    onFire = SimpleChange(previousState.onFire, true)
                }
                fun jump(lineNumber: Int) {
                    instructionIndexValue = SimpleChange(previousState.instructionIndex, lineNumber)
                }

                fun waitFor(ticks: Int) {
                    waitingForValue = SimpleChange(previousState.waitingFor, ticks)
                }

                fun decrementWaitFor() {
                    waitingForValue = SimpleChange(previousState.waitingFor, max(0, waitingFor - 1))
                }

                override val result get() = Change(
                    registers,
                    devices,
                    onFire,
                    instructionIndexValue,
                    waitingForValue
                )
            }
        }
    }
}

val DeviceStateChangeBuilder.program get() =
    aspect<Ic10ProgramAspect.State, Ic10ProgramAspect.State.Change.Builder>()

val IValue.asRegister get() = if (this is RegisterValue) this.value else throw Exception("Expected register")
val IValue.asDevice get() = if (this is DeviceValue) this.value else throw Exception("Expected device slot")
fun Ic10ProgramAspect.State.Change.Builder.getAsValue(value: IValue) = when (value) {
    is NumberValue -> value.value
    is RegisterValue -> get(value.value)
    else -> throw Exception("Expected value")
}
fun Ic10ProgramAspect.State.Change.Builder.getAsDeviceId(value: IValue) = when (value) {
    is NumberValue -> value.value.toLong()
    is RegisterValue -> getAsValue(value).toLong()
    is DeviceValue -> get(value.value)
    else -> throw Exception("Expected device slot or value")
}