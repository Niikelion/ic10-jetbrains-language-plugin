package com.niikelion.ic10_language.logic.aspects

import com.niikelion.ic10_language.logic.*
import com.niikelion.ic10_language.logic.devices.DeviceInfo
import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder
import com.niikelion.ic10_language.logic.state.*
import com.niikelion.ic10_language.utils.alsoLet
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

    private fun State.Change.Builder.executeOneInstruction(
        state: SimulationStateChangeBuilder,
        deviceId: Long,
        aspect: Ic10ProgramAspect,
        device: DeviceStateChangeBuilder
    ) {
        if (waitingFor > 0) return
        aspect.lineNumberPropertyId?.also { syncFromProperty(device.property(it).toInt()) }
        if (isOnFire || isIcError != null) return
        val line = aspect.code.lines.elementAtOrNull(instructionIndex)
        val action = line?.instruction?.action
        try {
            if (action != null) {
                val args = line.args
                    .map { it.resolve(this) ?: throw Exception("Error resolving $it") }
                    .toTypedArray()
                val (netId, net) = state.networkFor(deviceId) ?: Pair(0L, Network.single(setOf(deviceId)))
                InstructionContext(state, NetworkContext(netId, net, state, get(DeviceSlots.db)), deviceId).action(args)
            }
            if (!jumped) jump((instructionIndex + 1) % Constraints.MAX_LINES)
        } catch (e: Throwable) {
            setIcError(e.message ?: "Unknown error")
        }
        aspect.errorPropertyId?.also { device.setProperty(it, if (isIcError != null) 1.0 else 0.0) }
        aspect.lineNumberPropertyId?.also { device.setProperty(it, instructionIndex.toDouble()) }
    }

    override fun tick(state: SimulationStateChangeBuilder, deviceId: Long) {
        state.device(deviceId).alsoLet {
            val device = this
            aspect<State, State.Change.Builder, Unit> {
                repeat(Constraints.MAX_LINES) {
                    executeOneInstruction(state, deviceId, this@Ic10ProgramAspect, device)
                }
                if (waitingFor > 0) decrementWaitFor()
            }
        }
    }

    override fun tickEnd(state: SimulationStateChangeBuilder, deviceId: Long) {
        state.device(deviceId).alsoLet {
            aspect<State, State.Change.Builder, Unit> {
                if (waitingFor > 0) decrementWaitFor()
            }
        }
    }

    fun step(
        deviceId: Long,
        initial: SimulationState,
        deviceNetworks: Map<Long, Pair<Long, Network>>,
        deviceDefinitions: Map<Long, DeviceInfo> = emptyMap()
    ): Sequence<SimulationState.StateChange> = sequence {
        var current = initial
        var slot = 0
        while (slot < Constraints.MAX_LINES) {
            val change = current.change(deviceNetworks, deviceDefinitions) {
                val simBuilder = this
                device(deviceId).alsoLet {
                    val devBuilder = this
                    aspect<State, State.Change.Builder, Unit> {
                        executeOneInstruction(simBuilder, deviceId, this@Ic10ProgramAspect, devBuilder)
                    }
                }
            }
            yield(change)
            current = change.perform(current)
            slot++
            if ((current.devices[deviceId]?.aspect<State>()?.waitingFor ?: 0) > 0) break
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
        val icError: String? = null,
        val instructionIndex: Int = 0,
        val waitingFor: Int = 0
    ): DeviceAspect.State {
        override fun change(): Change.Builder = Change.Builder(this)

        override fun debuggerEntries(): List<Pair<String, Double>> = registers.map { (register, value) ->
            val aliases = Registers.aliasesFor(register)
            val name = if (aliases.isEmpty()) register.name
                else "${register.name} (${aliases.joinToString(", ")})"
            name to value
        }

        override val status: String? get() = when {
            icError != null -> "Runtime error: $icError"
            onFire -> "Halted: caught fire (hcf)"
            else -> null
        }

        class Change(
            val registers: Map<Register, SimpleChange<Double>> = emptyMap(),
            val devices: Map<DeviceSlot, SimpleChange<Long>> = emptyMap(),
            val onFire: SimpleChange<Boolean>? = null,
            val icError: SimpleChange<String?>? = null,
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
                        compose(source.icError, icError),
                        compose(source.instructionIndex, instructionIndex),
                        compose(source.waitingFor, waitingFor)
                    )
                }
            }

            override operator fun plus(other: DeviceAspect.State.Change): Change {
                if (other !is Change) return this
                return Change(
                    registers.composeWith(other.registers),
                    devices.composeWith(other.devices),
                    composeNullable(onFire, other.onFire),
                    composeNullable(icError, other.icError),
                    composeNullable(instructionIndex, other.instructionIndex),
                    composeNullable(waitingFor, other.waitingFor)
                )
            }

            private fun <V> composeNullable(a: SimpleChange<V>?, b: SimpleChange<V>?): SimpleChange<V>? = when {
                a == null -> b
                b == null -> a
                else -> a + b
            }

            class Builder(
                private val previousState: State
            ): DeviceAspect.State.Change.Builder, IProgramState {
                private val registers = mutableMapOf<Register, SimpleChange<Double>>()
                private val devices = mutableMapOf<DeviceSlot, SimpleChange<Long>>()
                private var onFire: SimpleChange<Boolean>? = null
                private var icError: SimpleChange<String?>? = null
                private var instructionIndexValue: SimpleChange<Int>? = null
                private var waitingForValue: SimpleChange<Int>? = null

                override fun get(register: Register): Double = registers[register]?.nextValue ?: previousState.registers[register] ?: 0.0
                fun get(deviceSlot: DeviceSlot): Long = devices[deviceSlot]?.nextValue ?: previousState.devices[deviceSlot] ?: 0
                val isOnFire get() = onFire?.nextValue ?: previousState.onFire
                val isIcError: String? get() = icError?.nextValue ?: previousState.icError
                val instructionIndex get() = instructionIndexValue?.nextValue ?: previousState.instructionIndex

                val waitingFor get() = waitingForValue?.nextValue ?: previousState.waitingFor

                fun set(register: Register, value: Double) {
                    val previous = previousState.registers[register] ?: return
                    registers[register] = SimpleChange(previous, value)
                }
                private var didJump = false
                val jumped get() = didJump

                fun lightOnFire() {
                    onFire = SimpleChange(previousState.onFire, true)
                }
                fun setIcError(message: String) {
                    icError = SimpleChange(previousState.icError, message)
                }
                fun syncFromProperty(lineNumber: Int) {
                    instructionIndexValue = SimpleChange(previousState.instructionIndex, lineNumber)
                }
                fun jump(lineNumber: Int) {
                    instructionIndexValue = SimpleChange(previousState.instructionIndex, lineNumber)
                    didJump = true
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
                    icError,
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
