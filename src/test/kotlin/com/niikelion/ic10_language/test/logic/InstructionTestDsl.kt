package com.niikelion.ic10_language.test.logic

import com.niikelion.ic10_language.logic.*
import com.niikelion.ic10_language.logic.NetworkContext
import com.niikelion.ic10_language.logic.Instructions
import kotlin.test.assertFailsWith
import com.niikelion.ic10_language.logic.aspects.Ic10DeviceMemoryAspect
import com.niikelion.ic10_language.logic.aspects.Ic10MemoryAspect
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.logic.devices.DeviceState
import com.niikelion.ic10_language.logic.state.NetworkState
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val TEST_DEVICE_ID = 0L
private const val TEST_NETWORK_ID = 0L
private const val STACK_SIZE = 512

// Channel0–Channel7 are LogicType values 165–172.
// We store network channels keyed by their LogicType value.
private const val CHANNEL0_LOGIC_TYPE = 165

/**
 * Entry point for instruction tests.
 *
 * Usage:
 * ```
 * simulate {
 *     setup {
 *         register("r0", 3.0)
 *         register("r1", 5.0)
 *     }
 *     exec("add", reg("r2"), reg("r0"), reg("r1"))
 *     assert { register("r2", 8.0) }
 * }
 * ```
 */
fun simulate(block: InstructionTestBuilder.() -> Unit) =
    InstructionTestBuilder().also(block).run()

@DslMarker
annotation class InstructionTestDsl

@InstructionTestDsl
class InstructionTestBuilder(private val compiler: ((String) -> ProgramCode)? = null) {
    private val deviceId = TEST_DEVICE_ID

    private val registers = mutableMapOf<Register, Double>()
    private val deviceSlotIds = mutableMapOf<DeviceSlot, Long>()
    private val stackContents = Array(STACK_SIZE) { 0.0 }

    private val extraDevices = mutableMapOf<Long, DeviceState>()
    private val networkChannels = mutableMapOf<Int, Double>()
    private val dataConnectedExtra = mutableSetOf<Long>()
    private val softConnectedExtra = mutableSetOf<Long>()

    private val steps = mutableListOf<(SimulationState, Map<Long, Pair<Long, Network>>) -> SimulationState>()
    private val assertions = mutableListOf<(SimulationState) -> Unit>()

    // --- Value factories ---

    fun num(value: Double): IValue = NumberValue(value)
    fun num(value: Int): IValue = NumberValue(value.toDouble())
    fun reg(name: String): IValue = RegisterValue(Registers.get(name) ?: error("Unknown register: $name"))
    fun device(name: String): IValue = DeviceValue(DeviceSlots.get(name) ?: error("Unknown device slot: $name"))
    /** Network reference: device slot + port index, e.g. channel("db", 0) → db:0. */
    fun channel(deviceName: String, portIndex: Int = 0): IValue =
        NetworkRefValue(DeviceSlots.get(deviceName) ?: error("Unknown device slot: $deviceName"), portIndex)

    /**
     * The LogicType value for channel [index] (0–7).
     * Use as the channel argument to `l`/`s` channel instructions:
     * `exec("s", channel("db", 0), channelType(2), num(4.0))` → s db:0 Channel2 4
     */
    fun channelType(index: Int): IValue = NumberValue((CHANNEL0_LOGIC_TYPE + index).toDouble())

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

        /** Add a secondary device on the test network. */
        fun addDevice(id: Long, properties: Map<Int, Double>, dataConnected: Boolean = true) {
            this@InstructionTestBuilder.extraDevices[id] = DeviceState(properties, emptyMap())
            if (dataConnected) this@InstructionTestBuilder.dataConnectedExtra.add(id)
            else this@InstructionTestBuilder.softConnectedExtra.add(id)
        }

        /**
         * Pre-set a channel value on the shared network.
         * [channelIndex] is 0–7; it maps to the LogicType key used by
         * the `l`/`s` channel instructions (Channel0=165 … Channel7=172).
         */
        fun networkChannel(channelIndex: Int, value: Double) {
            this@InstructionTestBuilder.networkChannels[CHANNEL0_LOGIC_TYPE + channelIndex] = value
        }
    }

    fun setup(block: SetupContext.() -> Unit) = SetupContext().apply(block)

    // --- Execution ---

    fun exec(name: String, vararg args: IValue) {
        val instruction = Instructions.get(name) ?: error("Unknown instruction: $name")
        exec(instruction, *args)
    }

    /**
     * Executes one instruction with game-like error handling: exceptions are caught and
     * translated to [Ic10ProgramAspect.State.icError], mirroring
     * [Ic10ProgramAspect.executeOneInstruction]. Use `assert { hasError() }` to verify
     * that the instruction faulted.
     */
    fun exec(instruction: Instruction, vararg args: IValue) {
        val action = instruction.action ?: error("Instruction '${instruction.name}' is not implemented")
        steps += { state, deviceNetworks ->
            val builder = SimulationStateChangeBuilder(state, deviceNetworks)
            val (netId, net) = builder.networkFor(deviceId) ?: Pair(TEST_NETWORK_ID, Network.single(setOf(deviceId)))
            val ctx = InstructionContext(builder, NetworkContext(netId, net, builder, deviceId), deviceId)
            try {
                ctx.action(args.toList().toTypedArray())
            } catch (e: Throwable) {
                ctx.program.setIcError(e.message ?: "Unknown error")
            }
            builder.stateChange.perform(state)
        }
    }

    /** Asserts that executing [name] with [args] throws an exception (e.g. blocked access). */
    fun execFails(name: String, vararg args: IValue) {
        val instruction = Instructions.get(name) ?: error("Unknown instruction: $name")
        val action = instruction.action ?: error("Instruction '${instruction.name}' is not implemented")
        steps += { state, deviceNetworks ->
            val builder = SimulationStateChangeBuilder(state, deviceNetworks)
            val (netId, net) = builder.networkFor(deviceId) ?: Pair(TEST_NETWORK_ID, Network.single(setOf(deviceId)))
            assertFailsWith<Exception> {
                InstructionContext(builder, NetworkContext(netId, net, builder, deviceId), deviceId).action(args.toList().toTypedArray())
            }
            state
        }
    }

    /**
     * Compiles [code] as an IC10 source program and runs it through a full simulation tick
     * (up to 128 instructions, same as the in-game IC housing).
     *
     * Requires a compiler to have been injected via the [simulate] overload that accepts one —
     * typically the private wrapper in a [com.intellij.testFramework.fixtures.BasePlatformTestCase] subclass.
     */
    fun compile(code: String) {
        val programCode = (compiler ?: error(
            "compiled() needs a compiler; wrap simulate { } in a helper that passes one"
        )).invoke(code)
        steps += { state, deviceNetworks ->
            val aspect = Ic10ProgramAspect(programCode)
            var current = state
            for (change in aspect.step(deviceId, current, deviceNetworks)) {
                current = change.perform(current)
            }
            current
        }
    }

    // --- Assertions ---

    @InstructionTestDsl
    class AssertContext(
        private val state: SimulationState,
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
        /** Asserts the register holds a value in the half-open range [[lo], [hi]). */
        fun registerInRange(name: String, lo: Double, hi: Double) {
            val register = Registers.get(name) ?: error("Unknown register: $name")
            val actual = programState.registers[register] ?: 0.0
            assertTrue(actual >= lo && actual < hi,
                "Register $name: expected value in [$lo, $hi) but was $actual")
        }
        fun sp(expected: Int) = register("sp", expected.toDouble())
        fun ra(expected: Int) = register("ra", expected.toDouble())
        fun stackAt(address: Int, expected: Double) =
            assertEquals(expected, memoryState.contents[address], "Stack[$address]")
        fun stackAt(address: Int, expected: Number) = stackAt(address, expected.toDouble())
        fun waitingFor(expected: Int) =
            assertEquals(expected, programState.waitingFor, "waitingFor")
        fun onFire() = assertTrue(programState.onFire, "Expected device to be on fire")
        fun notOnFire() = assertFalse(programState.onFire, "Expected device not to be on fire")
        fun hasError() = assertTrue(programState.icError != null, "Expected IC error")
        fun instructionIndex(expected: Int) =
            assertEquals(expected, programState.instructionIndex, "Instruction index")

        fun deviceProperty(deviceId: Long, propertyId: Int, expected: Double) {
            val actual = state.devices[deviceId]?.properties?.get(propertyId)
                ?: error("Device $deviceId or property $propertyId not found")
            assertEquals(expected, actual, "Device $deviceId property $propertyId")
        }

        fun networkChannel(channelIndex: Int, expected: Double) {
            val key = CHANNEL0_LOGIC_TYPE + channelIndex
            val actual = state.networks[TEST_NETWORK_ID]?.channels?.get(key)
                ?: error("Network or channel $channelIndex (key $key) not found")
            assertEquals(expected, actual, "Channel $channelIndex")
        }

        fun deviceSlot(slotName: String, expectedId: Long) {
            val slot = DeviceSlots.get(slotName) ?: error("Unknown slot: $slotName")
            val actual = programState.devices[slot] ?: 0L
            assertEquals(expectedId, actual, "Device slot $slotName")
        }
    }

    fun assert(block: AssertContext.() -> Unit) {
        assertions += { state ->
            val programState = state.devices[deviceId]
                ?.aspects?.get(Ic10ProgramAspect.State::class) as? Ic10ProgramAspect.State
                ?: error("Program state not found")
            val memoryState = state.devices[deviceId]
                ?.aspects?.get(Ic10MemoryAspect.State::class) as? Ic10DeviceMemoryAspect.State
                ?: error("Memory state not found")
            AssertContext(state, programState, memoryState).apply(block)
        }
    }

    // --- Runner ---

    fun run() {
        val programState = Ic10ProgramAspect.State(
            registers = Registers.all.associateWith { registers[it] ?: 0.0 },
            devices = DeviceSlots.all.associateWith { deviceSlotIds[it] ?: 0L },
        )
        val memoryState = Ic10DeviceMemoryAspect.State(
            contents = stackContents.copyOf()
        )

        val mainDevice = DeviceState(
            properties = emptyMap(),
            aspects = mapOf(
                Ic10ProgramAspect.State::class to programState,
                Ic10MemoryAspect.State::class to memoryState,
            )
        )

        val initialChannels = (0..7).associate { i ->
            (CHANNEL0_LOGIC_TYPE + i) to (networkChannels[CHANNEL0_LOGIC_TYPE + i] ?: 0.0)
        }

        val allData = (dataConnectedExtra + deviceId)
        val network = Network(dataConnected = allData, softConnected = softConnectedExtra)
        val deviceNetworks = (allData + softConnectedExtra).associateWith { Pair(TEST_NETWORK_ID, network) }

        var state = SimulationState(
            devices = mapOf(deviceId to mainDevice) + extraDevices,
            networks = mapOf(TEST_NETWORK_ID to NetworkState(initialChannels))
        )

        for (step in steps) {
            state = step(state, deviceNetworks)
        }

        for (assertion in assertions) {
            assertion(state)
        }
    }
}
