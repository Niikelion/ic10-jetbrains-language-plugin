package com.niikelion.ic10_language.test.logic

import com.niikelion.ic10_language.logic.*
import com.niikelion.ic10_language.logic.Instructions
import com.niikelion.ic10_language.logic.aspects.Ic10DeviceMemoryAspect
import com.niikelion.ic10_language.logic.aspects.Ic10MemoryAspect
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.logic.devices.DeviceState
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val TEST_DEVICE_ID = 0L
private const val STACK_SIZE = 512

/**
 * Entry point for instruction tests.
 *
 * Usage:
 * ```
 * instructionTest {
 *     setup {
 *         register("r0", 3.0)
 *         register("r1", 5.0)
 *     }
 *     exec("add", reg("r2"), reg("r0"), reg("r1"))
 *     assert {
 *         register("r2", 8.0)
 *     }
 * }
 * ```
 */
fun simulate(block: InstructionTestBuilder.() -> Unit) =
    InstructionTestBuilder().also(block).run()

@DslMarker
annotation class InstructionTestDsl

@InstructionTestDsl
class InstructionTestBuilder {
    private val deviceId = TEST_DEVICE_ID

    private val registers = mutableMapOf<Register, Double>()
    private val deviceSlotIds = mutableMapOf<DeviceSlot, Long>()
    private val stackContents = Array(STACK_SIZE) { 0.0 }

    private val steps = mutableListOf<(SimulationStateChangeBuilder) -> Unit>()
    private val assertions = mutableListOf<(SimulationState) -> Unit>()

    // --- Value factories ---

    fun num(value: Double): IValue = NumberValue(value)
    fun num(value: Int): IValue = NumberValue(value.toDouble())
    fun reg(name: String): IValue = RegisterValue(Registers.get(name) ?: error("Unknown register: $name"))
    fun device(name: String): IValue = DeviceValue(DeviceSlots.get(name) ?: error("Unknown device slot: $name"))

    // --- State setup ---

    @InstructionTestDsl
    inner class SetupContext {
        fun register(name: String, value: Double) {
            this@InstructionTestBuilder.registers[Registers.get(name) ?: error("Unknown register: $name")] = value
        }
        fun register(name: String, value: Number) = register(name, value.toDouble())
        fun stackAt(address: Int, value: Double) { this@InstructionTestBuilder.stackContents[address] = value }
        fun stackAt(address: Int, value: Number) = stackAt(address, value.toDouble())
        fun deviceSlot(name: String, id: Long) {
            this@InstructionTestBuilder.deviceSlotIds[DeviceSlots.get(name) ?: error("Unknown device slot: $name")] = id
        }
        fun sp(value: Int) = register("sp", value.toDouble())
        fun ra(value: Int) = register("ra", value.toDouble())
    }

    fun setup(block: SetupContext.() -> Unit) = SetupContext().apply(block)

    // --- Execution ---

    fun exec(name: String, vararg args: IValue) {
        val instruction = Instructions.get(name) ?: error("Unknown instruction: $name")
        exec(instruction, *args)
    }

    fun exec(instruction: Instruction, vararg args: IValue) {
        val action = instruction.action ?: error("Instruction '${instruction.name}' is not implemented")
        steps += { builder -> InstructionContext(builder, deviceId).action(args.toList().toTypedArray()) }
    }

    // --- Assertions ---

    @InstructionTestDsl
    inner class AssertContext(
        private val programState: Ic10ProgramAspect.State,
        private val memoryState: Ic10DeviceMemoryAspect.State
    ) {
        fun register(name: String, expected: Double, delta: Double = 1e-8) {
            val register = Registers.get(name) ?: error("Unknown register: $name")
            val actual = programState.registers[register] ?: 0.0
            if (delta == 0.0)
                assertEquals(expected, actual, "Register $name")
            else
                assertTrue(abs(actual - expected) <= delta,
                    "Register $name: expected $expected ± $delta but was $actual")
        }

        fun register(name: String, expected: Number) = register(name, expected.toDouble())
        fun sp(expected: Int) = register("sp", expected.toDouble())
        fun ra(expected: Int) = register("ra", expected.toDouble())
        fun stackAt(address: Int, expected: Double) =
            assertEquals(expected, memoryState.contents[address], "Stack[$address]")
        fun stackAt(address: Int, expected: Number) = stackAt(address, expected.toDouble())
        fun onFire() = assertTrue(programState.onFire, "Expected device to be on fire")
        fun notOnFire() = assertFalse(programState.onFire, "Expected device not to be on fire")
        fun instructionIndex(expected: Int) =
            assertEquals(expected, programState.instructionIndex, "Instruction index")
    }

    fun assert(block: AssertContext.() -> Unit) {
        assertions += { state ->
            val programState = state.devices[deviceId]
                ?.aspects?.get(Ic10ProgramAspect.State::class) as? Ic10ProgramAspect.State
                ?: error("Program state not found")
            val memoryState = state.devices[deviceId]
                ?.aspects?.get(Ic10MemoryAspect.State::class) as? Ic10DeviceMemoryAspect.State
                ?: error("Memory state not found")
            AssertContext(programState, memoryState).apply(block)
        }
    }

    // --- Runner ---

    fun run() {
        val programState = Ic10ProgramAspect.State(
            registers = Registers.all.associateWith { registers[it] ?: 0.0 },
            devices = DeviceSlots.all.associateWith { deviceSlotIds[it] ?: 0L },
            stack = Array(STACK_SIZE) { 0.0 }
        )
        val memoryState = Ic10DeviceMemoryAspect.State(
            contents = stackContents.copyOf()
        )

        val deviceState = DeviceState(
            properties = emptyMap(),
            aspects = mapOf(
                Ic10ProgramAspect.State::class to programState,
                Ic10MemoryAspect.State::class to memoryState,
            )
        )

        var state = SimulationState(devices = mapOf(deviceId to deviceState))

        for (step in steps) {
            val builder = SimulationStateChangeBuilder(state)
            step(builder)
            state = builder.stateChange.perform(state)
        }

        for (assertion in assertions) {
            assertion(state)
        }
    }
}