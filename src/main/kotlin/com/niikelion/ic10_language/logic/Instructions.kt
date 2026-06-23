package com.niikelion.ic10_language.logic

import com.niikelion.ic10_language.logic.aspects.*
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect.Companion.TICK_PER_SECOND
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder
import kotlin.math.*
import kotlin.random.Random

private val typeLabels = listOf("d?", "r?", "num")
private fun select(cond: Boolean, ifTrue: Double = 1.0, ifFalse: Double = 0.0) = if (cond) ifTrue else ifFalse
private fun aggregate(mode: Int, values: List<Double>): Double = when (mode) {
    0 -> if (values.isEmpty()) 0.0 else values.sum() / values.size
    1 -> values.sum()
    2 -> values.minOrNull() ?: 0.0
    3 -> values.maxOrNull() ?: 0.0
    else -> 0.0
}
typealias InstructionAction = InstructionContext.(args: Array<IValue>) -> Unit

private fun ap(a: Double, b: Double, c: Double): Boolean {
    val epsilon = Constants.get("epsilon")!!.value
    return abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8)
}

class Instruction(
    val name: String,
    val description: String,
    val arguments: List<Arg>,
    val isDeclaration: Boolean = false,
    val deprecationMessage: String? = null,
    val action: InstructionAction? = null
) {
    class Arg(val name: String, val type: ArgType) {
        companion object {
            fun name(name: String) = Arg(name, ArgType.Name)
            fun device(name: String) = Arg(name, ArgType.Device)
            fun value(name: String) = Arg(name, ArgType.Value)
            fun constant(name: String) = Arg(name, ArgType.Constant)
            fun variable(name: String) = Arg(name, ArgType.Variable)
            fun property(name: String) = Arg(name, ArgType.Property)
            fun slotProperty(name: String) = Arg(name, ArgType.SlotProperty)
        }
    }
    enum class ArgType {
        Variable {
            override val acceptsVariable = true
        },
        Device {
            override val acceptsDevice = true
            override val acceptsValue = true
            override val acceptsVariable = true
        },
        Value {
            override val acceptsValue = true
            override val acceptsVariable = true
        },
        Constant {
            override val acceptsValue = true
        },
        Name, Property, SlotProperty;

        open val acceptsDevice: Boolean = false
        open val acceptsValue: Boolean = false
        open val acceptsVariable: Boolean = false

        val typeNames get(): List<String> = when(this) {
            Name -> listOf("label")
            Property -> listOf("logicType")
            SlotProperty -> listOf("slotLogicType")
            else -> listOf(acceptsDevice, acceptsVariable, acceptsValue)
                .zip(typeLabels)
                .filter { (flag, _) -> flag }
                .map { (_, n) -> n }
        }
    }

    fun deprecate(msg: String) = Instruction(
        name = name,
        description = description,
        arguments = arguments,
        isDeclaration = isDeclaration,
        deprecationMessage = msg,
        action = action
    )
}

class InstructionContext(
    val global: SimulationStateChangeBuilder,
    val network: NetworkContext,
    private val deviceId: Long
) {
    val self by lazy { global.device(deviceId) }
    val program by lazy { self.program }
    val memory by lazy { self.memory }
}

@Suppress("SameParameterValue")
object Instructions {
    private fun device(name: String) = Instruction.Arg.device(name)
    private fun value(name: String) = Instruction.Arg.value(name)
    private fun constant(name: String) = Instruction.Arg.constant(name)
    private fun variable(name: String) = Instruction.Arg.variable(name)
    private fun property(name: String) = Instruction.Arg.property(name)
    private fun slotProperty(name: String) = Instruction.Arg.slotProperty(name)

    private val alias = Instruction.Arg.name("alias")
    private val inputValues = arrayOf("a", "b", "c", "d", "e").map { value(it) }
    private val resultVariable = variable("r")
    private val targetDevice = device("d")
    private val propertyName = property("property")
    private val slotPropertyName = slotProperty("property")
    private val batchMode = value("batchMode")

    private fun rawOp(name: String, arguments: Int, description: String, action: ((args: List<Double>) -> Double)? = null) =
        Instruction(
            name, "r = $description",
            listOf(resultVariable) + List(arguments) { inputValues[it] },
            action = action?.let { func ->
                { args ->
                    val value = func(List(arguments) { i -> program.getAsValue(args[i + 1]) })
                    program.set(args[0].asRegister, value)
                }
            }
        )
    private fun opNoArgs(name: String, description: String, action: () -> Double) =
        rawOp(name, 0, description) { action() }
    private fun op(name: String, description: String, action: (a: Double) -> Double) =
        rawOp(name, 1, description) { args -> action(args[0]) }
    private fun op(name: String, description: String, action: (a: Double, b: Double) -> Double) =
        rawOp(name, 2, description) { args -> action(args[0], args[1]) }
    private fun op(name: String, description: String, action: (a: Double, b: Double, c: Double) -> Double) =
        rawOp(name, 3, description) { args -> action(args[0], args[1], args[2]) }

    private fun branch(name: String, description: String, arguments: Int, relative: Boolean = false, call: Boolean = false, action: (InstructionContext.(args: List<Double>) -> Boolean)? = null) =
        branch(name, description, inputValues.take(arguments), relative, call, action?.let { func ->
            { args ->
                func(args.map(program::getAsValue))
            }
        })
    private fun branch(name: String, description: String, arguments: List<Instruction.Arg>, relative: Boolean, call: Boolean, action: (InstructionContext.(args: List<IValue>) -> Boolean)? = null) =
        Instruction(
            name,
            "${if (relative) "Relative branch" else "Branch" } to line ${arguments.last().name} if $description${if (call) " and store return pointer in ra" else "" }",
            arguments,
            action = action?.let { func ->
                { args ->
                    val shouldJump = func(args.take(arguments.size - 1))

                    if (shouldJump) {
                        val jumpTarget = program.getAsValue(args.last())
                        val normalizedJumpTarget = if (relative) program.instructionIndex + jumpTarget else jumpTarget
                        val returnAddress = program.instructionIndex + 1
                        program.jump(normalizedJumpTarget.toInt())
                        if (call)
                            program.set(Registers.ra, returnAddress.toDouble())
                    }
                }
            }
        )

    private fun branch(name: String, description: String, relative: Boolean = false, call: Boolean = false, action: (a: Double) -> Boolean) =
        branch(name, description, 2, relative, call) { args -> action(args[0]) }

    private fun branch(name: String, description: String, relative: Boolean = false, call: Boolean = false, action: (a: Double, b: Double) -> Boolean) =
        branch(name, description, 3, relative, call) { args -> action(args[0], args[1]) }

    private fun branch(name: String, description: String, relative: Boolean = false, call: Boolean = false, action: (a: Double, b: Double, c: Double) -> Boolean) =
        branch(name, description, 3, relative, call) { args -> action(args[0], args[1], args[2]) }

    private fun branchDevice(name: String, description: String, relative: Boolean = false, call: Boolean = false, action: (deviceId: Long) -> Boolean) =
        branch(name, "device d $description", listOf(targetDevice, value("a")), relative, call) { args -> action(program.get(args[0].asDevice)) }

    private val operations = arrayOf(
        op("abs", "|a|") { a -> abs(a) },
        op("acos", "arc cosine of a in radians") { a -> acos(a) },
        op("add", "a + b") { a, b -> a + b },
        Instruction(
            "alias",
            "Creates alias for originalName",
            listOf(alias, variable("originalName")),
            isDeclaration = true
        ) {},
        op("and", "a & b") { a, b -> (a.toValueBits() and b.toValueBits()).toDouble() },
        op("asin", "arc sine of a in radians") { a -> asin(a) },
        op("atan", "arc tangent of a in radians") { a -> atan(a) },
        op("atan2", "counter-clockwise angle between positive x axis and ray from (0,0) to (b, a)") { a, b -> atan2(a, b) },
        branch("bap", "abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8)", call = false) { a, b, c -> ap(a, b, c) },
        branch("bapal", "abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8)", call = true) { a, b, c -> ap(a, b, c) },
        branch("bapz", "abs(a) <= max(b * abs(a), epsilon * 8)", call = false) { a, b -> ap(a, 0.0, b) },
        branch("bapzal", "abs(a) <= max(b * abs(a), epsilon * 8)", call = true) { a, b -> ap(a, 0.0, b) },
        branchDevice("bdns", "is not set", call = false) { id -> id == 0L },
        branchDevice("bdnsal", "is not set", call = true) { id -> id == 0L },
        branch("bdnvl", "device d is not valid for a load instruction for property", listOf(targetDevice, propertyName, value("a")), relative = false, call = false) { args ->
            val targetId = program.getAsDeviceId(args[0])
            val property = program.getAsValue(args[1]).toInt()
            !network.canReadProperty(targetId, property)
        },
        branch("bdnvs", "device d is not valid for a store instruction for property", listOf(targetDevice, propertyName, value("a")), relative = false, call = false) { args ->
            val targetId = program.getAsDeviceId(args[0])
            val property = program.getAsValue(args[1]).toInt()
            !network.canWriteProperty(targetId, property)
        },
        branchDevice("bdse", "is set", call = false) { id -> id != 0L },
        branchDevice("bdseal", "device d is set", call = true) { id -> id != 0L },
        branch("beq", "a == b") { a, b -> a == b },
        branch("beqal", "a == b", call = true) { a, b -> a == b },
        branch("beqz", "a == 0") { a -> a == 0.0 },
        branch("beqzal", "a == 0", call = true) { a -> a == 0.0 },
        branch("bge", "a >= b") { a, b -> a >= b },
        branch("bgeal", "a >= b", call = true) { a, b -> a >= b },
        branch("bgez", "a >= 0") { a -> a >= 0.0 },
        branch("bgezal", "a >= 0", call = true) { a -> a >= 0.0 },
        branch("bgt", "a > b") { a, b -> a > b },
        branch("bgtal", "a > b", call = true) { a, b -> a > b },
        branch("bgtz", "a > 0") { a -> a > 0.0 },
        branch("bgtzal", "a > 0", call = true) { a -> a > 0.0 },
        branch("ble", "a <= b") { a, b -> a <= b },
        branch("bleal", "a <= b", call = true) { a, b -> a <= b },
        branch("blez", "a <= 0") { a -> a <= 0.0 },
        branch("blezal", "a <= 0", call = true) { a -> a <= 0.0 },
        branch("blt", "a < b") { a, b -> a < b },
        branch("bltal", "a < b", call = true) { a, b -> a < b },
        branch("bltz", "a < 0") { a -> a < 0.0 },
        branch("bltzal", "a < 0", call = true) { a -> a < 0.0 },
        branch("bna", "abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8)") { a, b, c -> !ap(a, b, c) },
        branch("bnaal", "abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8)", call = true) { a, b, c -> !ap(a, b, c) },
        branch("bnan", "a is not a number (NaN)") { a -> a.isNaN() },
        branch("bnaz", "abs(a) > max(b * abs(a), epsilon * 8)") { a, b -> !ap(a, 0.0, b) },
        branch("bnazal", "abs(a) > max(b * abs(a), epsilon * 8)", call = true) { a, b -> !ap(a, 0.0, b) },
        branch("bne", "a != b") { a, b -> a != b },
        branch("bneal", "a != b", call = true) { a, b -> a != b },
        branch("bnez", "a != 0") { a -> a != 0.0 },
        branch("bnezal", "a != 0", call = true) { a -> a != 0.0 },
        branch("brap", "abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8)", relative = true) { a, b, c -> ap(a, b, c) },
        branch("brapz", "abs(a) <= max(c * abs(a), epsilon * 8)", relative = true) { a, b -> ap(a, 0.0, b) },
        branch("brdns", "device is not set", listOf(targetDevice, value("a")), relative = true, call = false) { args ->
            program.get(args[0].asDevice) == 0L
        },
        branch("brdse", "device is set", listOf(targetDevice, value("a")), relative = true, call = false) { args ->
            program.get(args[0].asDevice) != 0L
        },
        branch("breq", "a == b", relative = true) { a, b -> a == b },
        branch("breqz", "a == 0", relative = true) { a -> a == 0.0 },
        branch("brge", "a >= b", relative = true) { a, b -> a >= b },
        branch("brgez", "a >= 0", relative = true) { a -> a >= 0.0 },
        branch("brgt", "a > b", relative = true) { a, b -> a > b },
        branch("brgtz", "a > 0", relative = true) { a -> a > 0.0 },
        branch("brle", "a <= b", relative = true) { a, b -> a <= b },
        branch("brlez", "a <= 0", relative = true) { a -> a <= 0.0 },
        branch("brlt", "a < b", relative = true) { a, b -> a < b },
        branch("brltz", "a < 0", relative = true) { a -> a < 0.0 },
        branch("brna", "abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8)", relative = true) { a, b, c -> !ap(a, b, c) },
        branch("brnan", "a is not a number (NaN)", relative = true) { a -> a.isNaN() },
        branch("brnaz", "abs(a) > max(b * abs(a), epsilon * 8)", relative = true) { a, b -> !ap(a, 0.0, b) },
        branch("brne", "a != b", relative = true) { a, b -> a != b },
        branch("brnez", "a != 0", relative = true) { a -> a != 0.0 },
        op("ceil", "smallest integer greater than a") { a -> ceil(a) },
        Instruction(
            "clr",
            "Clears the stack memory for the provided device",
            listOf(targetDevice)
        ) { args ->
            val deviceId = program.getAsDeviceId(args[0])

            global.device(deviceId).memory.clear()
        },
        Instruction("clrd", "Clears the stack memory for device with provided id", listOf(value("id")), deprecationMessage = "Use clr instead.") { args ->
            val deviceId = program.getAsValue(args[0]).toLong()

            global.device(deviceId).memory.clear()
        },
        op("cos", "cos(a)") { a -> cos(a) },
        Instruction(
            "define",
            "Creates alias for constant value",
            listOf(alias, constant("value")),
            isDeclaration = true
        ) {},
        op("div", "a / b") { a, b -> a / b },
        op("exp", "exp(a)") { a -> exp(a) },
        rawOp("ext", 3, "bit field from a, beginning at b for c length") { args ->
            val a = args[0].toLong()
            val b = args[1].toInt()
            val c = args[2].toInt()
            val mask = if (c >= 64) -1L else (1L shl c) - 1L
            ((a ushr b) and mask).toDouble()
        },
        op("floor", "largest integer less than a") { a -> floor(a) },
        Instruction(
            "get",
            "Reads the stack value of the provided device: r = d.stack[i]",
            listOf(resultVariable, targetDevice, value("i"))
        ) { args ->
            val target = args[0].asRegister
            val deviceId = program.getAsValue(args[1]).toLong()
            val address = program.getAsValue(args[2]).toInt()

            program.set(target, global.device(deviceId).memory.read(address))
        },
        Instruction(
            "getd",
            "Reads the stack value of the device indicated by id: r = getDevice(id).stack[i]",
            listOf(resultVariable, value("id"), value("i"))
        ) { args ->
            val target = args[0].asRegister
            val deviceId = program.getAsValue(args[1]).toLong()
            val address = program.getAsValue(args[2]).toInt()

            program.set(target, global.device(deviceId).memory.read(address))
        },
        Instruction(
            "hcf",
            "Self-destructs the device",
            listOf()
        ) {
            program.lightOnFire()
        },
        Instruction(
            "ins",
            "Inserts a bit field of a into r, beginning at b for c length",
            listOf(resultVariable, value("a"), value("b"), value("c"))
        ) { args ->
            val current = program.get(args[0].asRegister).toLong()
            val a = program.getAsValue(args[1]).toLong()
            val b = program.getAsValue(args[2]).toInt()
            val c = program.getAsValue(args[3]).toInt()
            val mask = if (c >= 64) -1L else (1L shl c) - 1L
            val result = (current and (mask shl b).inv()) or ((a and mask) shl b)
            program.set(args[0].asRegister, result.toDouble())
        },
        Instruction(
            "j",
            "Jump to line",
            listOf(value("line"))
        ) { args ->
            program.jump(program.getAsValue(args[0]).toInt())
        },
        Instruction(
            "jal",
            "Jump to line a and store return pointer in ra",
            listOf(value("a"))
        ) { args ->
            val targetLine = program.getAsValue(args[0]).toInt()

            program.set(Registers.ra, (program.instructionIndex + 1).toDouble())
            program.jump(targetLine)
        },
        Instruction(
            "jr",
            "Relative jump by a lines",
            listOf(value("a"))
        ) { args ->
            val offset = program.getAsValue(args[0]).toInt()

            program.jump(program.instructionIndex + offset)
        },
        Instruction(
            "l",
            "Loads property from provided device: r = d.property",
            listOf(resultVariable, targetDevice, propertyName)
        ) { args ->
            val result = args[0].asRegister
            when (val deviceArg = args[1]) {
                is NetworkRefValue -> {
                    // db:0 is the network reference (device + port); args[2] is the channel
                    // (e.g. Channel2 = LogicType 167). The port index is intentionally unused
                    // while the simulation only supports one network per device.
                    val slotDeviceId = program.get(deviceArg.slot)
                    val channelKey = program.getAsValue(args[2]).toInt()
                    val channels = network.channelsOf(slotDeviceId) ?: throw DeviceNotOnNetworkError(slotDeviceId)
                    program.set(result, channels.readChannel(channelKey))
                }
                else -> {
                    val targetId = program.getAsDeviceId(deviceArg)
                    val property = program.getAsValue(args[2]).toInt()
                    program.set(result, network.device(targetId).property(property))
                }
            }
        },
        Instruction(
            "lb",
            "Loads property from all devices with given typeHash and aggregates by batchMode: r = batchMode(findDevicesByType(typeHash).map(d -> d.property))",
            listOf(resultVariable, value("typeHash"), propertyName, batchMode)
        ) { args ->
            val result = args[0].asRegister
            val typeHash = program.getAsValue(args[1]).toLong()
            val property = program.getAsValue(args[2]).toInt()
            val mode = program.getAsValue(args[3]).toInt()
            program.set(result, aggregate(mode, network.devicesByType(typeHash).map { it.property(property) }))
        },
        Instruction(
            "lbn",
            "Loads property from all devices with given typeHash and nameHash and aggregates by batchMode: r = batchMode(findDevicesByTypeAndName(typeHash, nameHash).map(d -> d.property))",
            listOf(resultVariable, value("typeHash"), value("nameHash"), propertyName, batchMode)
        ) { args ->
            val result = args[0].asRegister
            val typeHash = program.getAsValue(args[1]).toLong()
            val nameHash = program.getAsValue(args[2]).toLong()
            val property = program.getAsValue(args[3]).toInt()
            val mode = program.getAsValue(args[4]).toInt()
            program.set(result, aggregate(mode, network.devicesByTypeAndName(typeHash, nameHash).map { it.property(property) }))
        },
        /* TODO: implement */
        Instruction(
            "lbns",
            "Loads property from given slot index of all devices with given typeHash and nameHash and aggregates by batchMode: r = batchMode(findDevicesByTypeAndName(typeHash, nameHash).map(d -> d.slot[slotIndex].property))",
            listOf(resultVariable, value("typeHash"), value("nameHash"), value("slotIndex"), slotPropertyName, batchMode),
            action = null
        ),
        /* TODO: implement */
        Instruction(
            "lbs",
            "Loads property from given slot index of all devices with given typeHash and aggregates by batchMode: r = batchMode(findDevicesByType(typeHash).map(d -> d.slot[slotIndex].property))",
            listOf(resultVariable, value("typeHash"), value("slotIndex"), slotPropertyName, batchMode),
            action = null
        ),
        Instruction(
            "ld",
            "Loads property from device indicated by id: r = getDevice(id).property",
            listOf(resultVariable, value("id"), propertyName)
        ) { args ->
            val result = args[0].asRegister
            val targetId = program.getAsValue(args[1]).toLong()
            val property = program.getAsValue(args[2]).toInt()
            program.set(result, network.device(targetId).property(property))
        }.deprecate("Use l instead."),
        op("lerp", "a * c + (1 - c) * b") { a, b, c -> a * c + (1 - c) * b },
        op("log", "log(a)") { a -> ln(a) },
        Instruction("lr", "Loads given reagent count from device based on reagentMode(Contents(0) - currently in the device, Required(1) - missing from the recipe, Recipe(2) - required by the recipe): r = d.reagentMode.count(reagentId)", listOf(
            resultVariable, targetDevice, value("reagentMode"), value("reagentId")
        )) { args ->
            val result = args[0].asRegister
            val deviceId = program.getAsDeviceId(args[1])
            val mode = program.getAsValue(args[2]).toInt()
            val reagentHash = program.getAsValue(args[3]).toLong()
            val recipeHashPropId = Device.properties["RecipeHash"]
            val quantity = when (mode) {
                0 -> network.device(deviceId).crafting?.read(reagentHash) ?: 0.0
                1 -> {
                    val devicePrefabHash = global.prefabHash(deviceId) ?: 0L
                    val selectedItem = recipeHashPropId?.let { network.device(deviceId).property(it).toLong() } ?: 0L
                    val required = if (selectedItem != 0L) Reagents.recipeFor(devicePrefabHash, selectedItem)?.get(reagentHash) ?: 0.0 else 0.0
                    maxOf(0.0, required - (network.device(deviceId).crafting?.read(reagentHash) ?: 0.0))
                }
                2 -> {
                    val devicePrefabHash = global.prefabHash(deviceId) ?: 0L
                    val selectedItem = recipeHashPropId?.let { network.device(deviceId).property(it).toLong() } ?: 0L
                    if (selectedItem != 0L) Reagents.recipeFor(devicePrefabHash, selectedItem)?.get(reagentHash) ?: 0.0 else 0.0
                }
                else -> 0.0
            }
            program.set(result, quantity)
        },
        /* TODO: implement */
        Instruction("ls", "Loads property from given slot index of provided device: r = d.slot[slotIndex].property", listOf(
            resultVariable, targetDevice, value("slotIndex"), slotPropertyName
        ), action = null),
        op("max", "max of a or b") { a, b -> max(a, b) },
        op("min", "min of a or b") { a, b -> min(a, b) },
        op("mod", "a mod b(NOT the same as a % b)") { a, b -> if (a > 0 || b > 0) a.mod(b.absoluteValue) else a },
        op("move", "a") { a -> a },
        op("mul", "a * b") { a, b -> a * b },
        op("nor", "!(a || b)") { a, b -> (a.toValueBits() or b.toValueBits()).inv().toDouble() },
        op("not", "!a") { a -> a.toValueBits().inv().toDouble() },
        op("or", "a || b") { a, b -> (a.toValueBits() or b.toValueBits()).toDouble() },
        Instruction("peek", "reads the value at the top of the stack", listOf(resultVariable)) { args ->
            val result = args[0].asRegister
            val stackPointer = program.get(Registers.sp).toInt()

            program.set(result, memory.read(stackPointer - 1))
        },
        Instruction(
            "poke",
            "Stores the provided value at the provided address in the stack: db.stack[address] = value",
            listOf(value("address"), value("value"))
        ) { args ->
            val address = program.getAsValue(args[0]).toInt()
            val value = program.getAsValue(args[1])

            memory.write(address, value)
        },
        Instruction("pop", "reads the value at the top of the stack and decrements sp", listOf(resultVariable)) { args ->
            val result = args[0].asRegister
            val stackPointer = program.get(Registers.sp).toInt() - 1

            program.set(result, memory.read(stackPointer))
            program.set(Registers.sp, stackPointer.toDouble())
        },
        op("pow", "a to the power of b") { a, b -> a.pow(b) },
        Instruction("push", "Stores the provided value at the top of the stack and increments sp", listOf(value("value"))) { args ->
            val stackPointer = program.get(Registers.sp).toInt()
            val value = program.getAsValue(args[0])

            memory.write(stackPointer, value)
            program.set(Registers.sp, stackPointer + 1.0)
        },
        Instruction("put", "Stores the provided value at the provided address in the stack of the provided device: d.stack[address] = value", listOf(
            targetDevice, value("address"), value("value")
        )) { args ->
            val deviceId = program.getAsDeviceId(args[0])
            val address = program.getAsValue(args[1]).toInt()
            val value = program.getAsValue(args[2])

            global.device(deviceId).memory.write(address, value)
        },
        Instruction("putd", "Stores the provided value at the provided address in the stack of device with provided id: findDeviceById(id).stack[address] = value", listOf(
            targetDevice, value("address"), value("value")
        ), deprecationMessage = "Use put instead.") { args ->
            val deviceId = program.getAsValue(args[0]).toLong()
            val address = program.getAsValue(args[1]).toInt()
            val value = program.getAsValue(args[2])

            global.device(deviceId).memory.write(address, value)
        },
        opNoArgs("rand", "random value x, so that 0 <= x < 1") { Random.nextDouble() },
        Instruction("rmap", "Finds id of item that gives given reagent when placed into the machine, for example for Autolathe and id of Iron it will output id of ItemIronIngot", listOf(
            resultVariable, targetDevice, value("reagentId")
        )) { args ->
            val result = args[0].asRegister
            val deviceId = program.getAsDeviceId(args[1])
            val reagentHash = program.getAsValue(args[2]).toLong()
            val devicePrefabHash = global.prefabHash(deviceId) ?: throw DeviceNotFoundError(deviceId)
            program.set(result, (Reagents.findSourcePrefabHash(devicePrefabHash, reagentHash) ?: 0L).toDouble())
        },
        op("round", "a rounded to the nearest integer") { a -> round(a) },
        Instruction(
            "s",
            "Writes value to the property of the device: d.property = value",
            listOf(targetDevice, propertyName, value("value"))
        ) { args ->
            when (val deviceArg = args[0]) {
                is NetworkRefValue -> {
                    // db:0 is the network reference (device + port); args[1] is the channel
                    // (e.g. Channel2 = LogicType 167) and args[2] is the value to write.
                    val slotDeviceId = program.get(deviceArg.slot)
                    val channelKey = program.getAsValue(args[1]).toInt()
                    val value = program.getAsValue(args[2])
                    val channels = network.channelsOf(slotDeviceId) ?: throw DeviceNotOnNetworkError(slotDeviceId)
                    channels.writeChannel(channelKey, value)
                }
                else -> {
                    val target = program.getAsDeviceId(deviceArg)
                    val property = program.getAsValue(args[1]).toInt()
                    val value = program.getAsValue(args[2])
                    network.device(target).setProperty(property, value)
                }
            }
        },
        op("sap", "1 if abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8), 0 otherwise") { a, b, c -> select(ap(a, b, c)) },
        op("sapz", "1 if abs(a) <= max(b * abs(a), epsilon * 8), 0 otherwise") { a, b -> select(ap(a, 0.0, b)) },
        Instruction("sb", "Writes value to property of all devices with given typeHash", listOf(value("typeHash"), propertyName, value("value"))) { args ->
            val typeHash = program.getAsValue(args[0]).toLong()
            val property = program.getAsValue(args[1]).toInt()
            val value = program.getAsValue(args[2])
            network.devicesByType(typeHash).forEach { it.setProperty(property, value) }
        },
        Instruction("sbn", "Writes value to property of all devices with given typeHash and nameHash", listOf(value("typeHash"), value("nameHash"), propertyName, value("value"))) { args ->
            val typeHash = program.getAsValue(args[0]).toLong()
            val nameHash = program.getAsValue(args[1]).toLong()
            val property = program.getAsValue(args[2]).toInt()
            val value = program.getAsValue(args[3])
            network.devicesByTypeAndName(typeHash, nameHash).forEach { it.setProperty(property, value) }
        },
        /* TODO: implement */
        Instruction("sbs", "Writes value to property of given slot on all devices with given typeHash", listOf(value("typeHash"), value("slotIndex"), slotPropertyName, value("value")), action = null),
        Instruction(
            "sd",
            "Writes value to property of the device indicated by id: getDevice(id).property = value",
            listOf(value("id"), propertyName, value("value"))
        ) { args ->
            val target = program.getAsValue(args[0]).toLong()
            val property = program.getAsValue(args[1]).toInt()
            val value = program.getAsValue(args[2])
            network.device(target).setProperty(property, value)
        }.deprecate("Use s instead"),
        Instruction("sdns", "r = 1 if device d is not set, 0 otherwise", listOf(resultVariable, targetDevice)) { args ->
            val target = args[0].asRegister
            val deviceSlot = args[1].asDevice

            program.set(target, select (program.get(deviceSlot) == 0L))
        },
        Instruction("sdse", "r = 1 if device d is set, 0 otherwise", listOf(resultVariable, targetDevice)) { args ->
            val target = args[0].asRegister
            val deviceSlot = args[1].asDevice

            program.set(target, select (program.get(deviceSlot) != 0L))
        },
        op("select", "a != 0 ? b : c") { a, b, c -> select(a != 0.0, b, c) },
        op("seq", "a == b ? 1 : 0") { a, b -> select(a == b) },
        op("seqz", "a == 0 ? 1 : 0") { a -> select(a == 0.0) },
        op("sge", "a >= b ? 1 : 0") { a, b -> select(a >= b) },
        op("sgez", "a >= 0 ? 1 : 0") { a -> select(a >= 0.0) },
        op("sgt", "a > b ? 1 : 0") { a, b -> select(a > b) },
        op("sgtz", "a > 0 ? 1 : 0") { a -> select(a > 0.0) },
        op("sin", "sin(a)") { a -> sin(a) },
        op("sla", "a << b, vacated bits are filled with a copy of the sign bit") { a, b -> (a.toLong() shl b.toInt()).toDouble() },
        op("sle", "a <= b ? 1 : 0") { a, b -> select(a <= b) },
        Instruction("sleep", "Pauses execution on the IC for time seconds", listOf(value("time"))) { args ->
            val time = program.getAsValue(args[0])

            program.waitFor(ceil(max(time, 0.0) * TICK_PER_SECOND).toInt())
        },
        op("slez", "a <= 0 ? 1 : 0") { a -> select(a <= 0.0) },
        op("sll", "a << b") { a, b -> (a.toLong() shl b.toInt()).toDouble() },
        op("slt", "a < b ? 1 : 0") { a, b -> select(a < b) },
        op("sltz", "a < 0 ? 1 : 0") { a -> select(a < 0.0) },
        op("sna", "abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8) ? 1 : 0") { a, b, c -> select(!ap(a, b, c)) },
        op("snan", "1 if a is NaN, 0 otherwise") { a -> select(a.isNaN()) },
        op("snanz", "1 if a is not NaN, 0 otherwise") { a -> select(!a.isNaN()) },
        op("snaz", "abs(a) > max(b * abs(a), epsilon * 8) ? 1 : 0") { a, b -> select(!ap(a, 0.0, b)) },
        op("sne", "a != b ? 1 : 0") { a, b -> select(a != b) },
        op("snez", "a != 0 ? 1 : 0") { a -> select(a != 0.0) },
        op("sqrt", "sqrt(a)") { a -> sqrt(a) },
        op("sra", "a >> b, vacated bits are filled with a copy of the sign bit") { a, b -> (a.toLong() shr b.toInt()).toDouble() },
        op("srl", "a >> b") { a, b -> (a.toValueBits() ushr b.toInt()).toDouble() },
        /* TODO: implement */
        Instruction("ss", "Writes value to property of given slot on the provided device", listOf(targetDevice, value("slotIndex"), slotPropertyName, value("value")), action = null),
        op("sub", "a - b") { a, b -> a - b },
        op("tan", "tan(a)") { a -> tan(a) },
        op("trunc", "a with fractional part removed") { a -> truncate(a) },
        op("xor", "a ^ b") { a, b -> (a.toValueBits() xor b.toValueBits()).toDouble() },
        Instruction("yield", "Pauses execution for 1 tick", listOf()) {
            program.waitFor(1)
        }
    ).associateBy(Instruction::name)
    fun get(name: String) = operations[name]
    val all get() = operations.values.toList()
}

private fun Double.toValueBits(): Long = toLong() and ((1L shl 54) - 1L)