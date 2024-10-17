package com.niikelion.ic10_language

data class Instruction(val name: String, val description: String, val arguments: List<Arg> = listOf()) {
    data class Arg(val name: String, val type: ArgType) {
        fun getTooltipText(): String = name

        companion object {
            fun name(name: String) = Arg(name, ArgType.Name)
            fun device(name: String) = Arg(name, ArgType.Device)
            fun value(name: String) = Arg(name, ArgType.Value)
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
            override val acceptsVariable = true
        },
        Value {
            override val acceptsValue = true
            override val acceptsVariable = true
        },
        Name, Property, SlotProperty;

        open val acceptsDevice: Boolean = false
        open val acceptsValue: Boolean = false
        open val acceptsVariable: Boolean = false
    }

    private val htmlForName = "<b>$name ${arguments.joinToString(" ") { it.getTooltipText() }}</b>"
    private val htmlForDescription = "<i>$description</i>"
    private fun wrapInHtml(content: String) = "<html><body>$content</body></html>"

    fun getTooltipText(): String = wrapInHtml("$htmlForName<br/>$htmlForDescription")
}

object Instructions {
    private fun name(name: String) = Instruction.Arg.name(name)
    private fun device(name: String) = Instruction.Arg.device(name)
    private fun value(name: String) = Instruction.Arg.value(name)
    private fun variable(name: String) = Instruction.Arg.variable(name)
    private fun property(name: String) = Instruction.Arg.property(name)
    private fun slotProperty(name: String) = Instruction.Arg.slotProperty(name)

    private val inputValues = arrayOf("a", "b", "c", "d", "e").map { value(it) }
    private val resultVariable = variable("r")
    private val targetDevice = device("d")
    private val propertyName = property("property")
    private val slotPropertyName = slotProperty("property")
    private val batchMode = value("batchMode")

    private fun op(name: String, arguments: Int, description: String) =
        Instruction(name, "r = $description", listOf(resultVariable) + List(arguments) { inputValues[it] })
    private fun branch(name: String, description: String, arguments: Int, relative: Boolean, call: Boolean) =
        Instruction(
            name,
            "${if (relative) "Relative branch" else "Branch" } to line ${inputValues[arguments-1].name} if $description${if (call) " and store return pointer in ra" else "" }",
            List(arguments) { inputValues[it] }
        )

    private val operations = arrayOf(
        op("abs", 1, "|a|"),
        op("acos", 1, "arc cosine of a in radians"),
        op("add", 2, "a + b"),
        Instruction("alias", "Creates alias for originalName", listOf(name("alias"), variable("originalName"))),
        op("and", 2, "a && b"),
        op("asin", 1, "arc sine of a in radians"),
        op("atan", 1, "arc tangent of a in radians"),
        op("atan2", 2, "counter-clockwise angle between positive x axis and ray from (0,0) to (b, a)"),
        branch("bap", "abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8)", 4, false, false),
        branch("bapal", "abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8)", 4, false ,true),
        Instruction("bapz", "Branch to line c if abs(a) <= max(b * abs(a), epsilon * 8)", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bapzal", "Branch to line c if abs(a) <= max(b * abs(a), epsilon * 8) and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bdns", "Branch to line a if device d is not set", listOf(targetDevice, value("a"),)),
        Instruction("bdnsal", "Branch to line a if device d is not set and store return pointer in ra", listOf(targetDevice, value("a"),)),
        Instruction("bdse", "Branch to line a if device d is set", listOf(targetDevice, value("a"),)),
        Instruction("bdseal", "Branch to line a if device d is set and store return pointer in ra", listOf(targetDevice, value("a"),)),
        Instruction("beq", "Branch to line c if a == b", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("beqal", "Branch to line c if a == b and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("beqz", "Branch to line b if a == 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("beqzal", "Branch to line b if a == 0 amd store return pointer in ra", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("bge", "Branch to line c if a >= b", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bgeal", "Branch to line c if a >= b and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bgez", "Branch to line b if a >= 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("bgezal", "Branch to line b if a >= 0 and store return pointer in ra", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("bgt", "Branch to line c if a > b", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bgtal", "Branch to line c if a > b and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bgtz", "Branch to line b if a > 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("bgtzal", "Branch to line b if a > 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("ble", "Branch to line c if a <= b", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bleal", "Branch to line c if a <= b and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("blez", "Branch to line b if a <= 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("blezal", "Branch to line b if a <= 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("blt", "Branch to line c if a < b", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bltal", "Branch to line c if a < b and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bltz", "Branch to line b if a < 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("bltzal", "Branch to line b if a < 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("bna", "Branch to line d if abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8)", listOf(
            value("a"),
            value("b"),
            value("c"),
            value("d"),
        )),
        Instruction("bnaal", "Branch to line d if abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8) and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
            value("d"),
        )),
        Instruction("bnan", "Branch to line b if a is not a number (NaN)", listOf(
            value("a"),
            value("b")
        )),
        Instruction("bnaz", "Branch to line c if abs(a) > max(b * abs(a), epsilon * 8)", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bnazal", "Branch to line c if abs(a) > max(b * abs(a), epsilon * 8) and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bne", "Branch to line c if a != b", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bneal", "Branch to line c if a != b and store return pointer in ra", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("bnez", "Branch to line b if a != 0", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("bnezal", "Branch to line b if a != 0 and store return pointer in ra", listOf(
            value("a"),
            value("b"),
        )),
        Instruction("brap", "Relative branch to line d if abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8)", listOf(
            value("a"),
            value("b"),
            value("c"),
            value("d"),
        )),
        Instruction("brapz", "Relative branch to line c if abs(a) <= max(c * abs(a), epsilon * 8)", listOf(
            value("a"),
            value("b"),
            value("c"),
        )),
        Instruction("brdns", "Relative jump to line a if device is not set", listOf(targetDevice, value("a"))),
        Instruction("brdse", "Relative jump to line a if device is set", listOf(targetDevice, value("a"))),
        branch("breq", "a == b", 3, true, false),
        branch("breqz", "a == 0", 2, true, false),
        branch("brge", "a >= b", 3, true, false),
        branch("brgez", "a >= 0", 2, true, false),
        branch("brgt", "a > b", 3, true, false),
        branch("brgtz", "a > 0", 2, true, false),
        branch("brle", "a <= b", 3, true, false),
        branch("brlez", "a <= 0", 2, true, false),
        branch("brlt", "a < b", 3, true, false),
        branch("brltz", "a < 0", 2, true, false),
        branch("brna", "abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8)", 4, true, false),
        branch("brnan", "a is not a number (NaN)", 2, true, false),
        branch("brnaz", "abs(a) > max(b * abs(a), epsilon * 8)", 3, true, false),
        branch("brne", "a != b", 3, true, false),
        branch("brnez", "a != 0", 2, true, false),
        op("ceil", 1, "smallest integer greater than a"),
        Instruction("clr", "Clears the stack memory for the provided device", listOf(targetDevice)),
        Instruction("clrd", "Clears the stack memory for device with provided id", listOf(value("id"))),
        op("cos", 1, "cos(a)"),
        Instruction("define", "Creates alias for constant value", listOf(name("alias"), value("value"))),
        op("div", 2, "a / b"),
        op("exp", 1, "exp(a)"),
        op("floor", 1, "largest integer less than a"),
        Instruction("get", "Reads the stack value of the provided value: r = d.stack[i]", listOf(resultVariable, targetDevice, value("i"))),
        Instruction("getd", "Reads the stack value of device with provided id: r = findDeviceById(id).stack[i]", listOf(resultVariable, value("id"), value("i"))),
        Instruction("hcf", "Self-destructs the device"),
        Instruction("j", "Jump to line a", listOf(value("a"))),
        Instruction("jal", "Jump to line a and store return pointer in ra", listOf(value("a"))),
        Instruction("jr", "Relative jump by a lines", listOf(value("a"))),
        Instruction(
            "l",
            "Loads property from provided device: r = d.property",
            listOf(resultVariable, targetDevice, propertyName)
        ),
        Instruction(
            "lb",
            "Loads property from all devices with given typeHash and aggregates by batchMode: r = batchMode(findDevicesByType(typeHash).map(d -> d.property))",
            listOf(resultVariable, value("typeHash"), propertyName, batchMode)
        ),
        Instruction(
            "lbn",
            "Loads property from all devices with given typeHash and nameHash and aggregates by batchMode: r = batchMode(findDevicesByTypeAndName(typeHash, nameHash).map(d -> d.property))",
            listOf(resultVariable, value("typeHash"), value("nameHash"), propertyName, batchMode)
        ),
        Instruction(
            "lbns",
            "Loads property from given slot index of all devices with given typeHash and nameHash and aggregates by batchMode: r = batchMode(findDevicesByTypeAndName(typeHash, nameHash).map(d -> d.slot[slotIndex].property))",
            listOf(resultVariable, value("typeHash"), value("nameHash"), value("slotIndex"), slotPropertyName, batchMode)
        ),
        Instruction(
            "lbs",
            "Loads property from given slot index of all devices with given typeHash and aggregates by batchMode: r = batchMode(findDevicesByType(typeHash).map(d -> d.slot[slotIndex].property))",
            listOf(resultVariable, value("typeHash"), value("slotIndex"), slotPropertyName, batchMode)
        ),
        Instruction(
            "ld",
            "Loads property from device with provided id: r = findDeviceById(id).property",
            listOf(resultVariable, value("id"), propertyName, batchMode)
        ),
        op("log", 1, "log(a)"),
        Instruction("lr", "Loads given reagent count from device based on reagentMode(Contents(0) - currently in the device, Required(1) - missing from the recipe, Recipe(2) - required by the recipe): r = d.reagentMode.count(reagentId)", listOf(resultVariable, targetDevice, value("reagentMode"), value("reagentId"))),
        Instruction("ls", "Loads property from given slot index of provided device: r = d.slot[slotIndex].property", listOf(resultVariable, targetDevice, value("slotIndex"), slotPropertyName)),
        op("max", 2, "max of a or b"),
        op("min", 2, "min of a or b"),
        op("mod", 2, "a mod b(not: NOT the same as a % b)"),
        op("move", 1, "b"),
        op("mul", 2, "a * b"),
        op("nor", 2, "!(a || b)"),
        op("not", 1, "!a"),
        op("or", 2, "a || b"),
        op("peek", 0, "value at the top of the stack"),
        Instruction("poke", "Store the provided value at the provided address in the stack: db.stack[address] = value", listOf(value("address"), value("value"))),
        op("pop", 0, "the value at the top of the stack and decrements sp"),
        Instruction("push", "Stores the provided value at the top of the stack and increments sp", listOf(value("value"))),
        Instruction("put", "Stores the provided value at the provided address in the stack of the provided device: d.stack[address] = value", listOf(targetDevice, value("address"), value("value"))),
        Instruction("putd", "Stores the provided value at the provided address in the stack of device with provided id: findDeviceById(id).stack[address] = value", listOf(targetDevice, value("address"), value("value"))),
        op("rand", 0, "random value x, so that 0 <= x < 1"),
        Instruction("rmap", "Finds id of item that gives given reagent when placed into the machine, for example for Autolathe and id of Iron it will output id of ItemIronIngot", listOf(resultVariable, targetDevice, value("reagentId"))),
        op("round", 1, "a rounded to the nearest integer"),
        Instruction("s", "Writes value to property of device: d.property = value", listOf(targetDevice, propertyName, value("value"))),
        op("sap", 3, "1 if abs(a - b) <= max(c * max(abs(a), abs(b)), epsilon * 8), 0 otherwise"),
        op("sapz", 2, "1 if abs(a) <= max(c * abs(a), epsilon * 8), 0 otherwise"),
        Instruction("sb", "Writes value to property of all devices with given typeHash", listOf(value("typeHash"), propertyName, value("value"))),
        Instruction("sbn", "Writes value to property of all devices with given typeHash and nameHash", listOf(value("typeHash"), value("nameHash"), propertyName, value("value"))),
        Instruction("sbs", "Writes value to property of given slot on all devices with given typeHash", listOf(value("typeHash"), value("slotIndex"), slotPropertyName, value("value"))),
        Instruction("sd", "Writes value to property of device with given id", listOf(value("id"), propertyName, value("value"))),
        Instruction("sdns", "r = 1 if device is not set, 0 otherwise", listOf(resultVariable, targetDevice)),
        Instruction("sdse", "r = 1 if device is set, 0 otherwise", listOf(resultVariable, targetDevice)),
        op("select", 3, "a != 0 ? b : c"),
        op("seq", 2, "a == b ? 1 : 0"),
        op("seqz", 1, "a == 0 ? 1 : 0"),
        op("sge", 2, "a >= b ? 1 : 0"),
        op("sgez", 1, "a >= 0 ? 1 : 0"),
        op("sgt", 2, "a > b ? 1 : 0"),
        op("sgtz", 1, "a > 0 ? 1 : 0"),
        op("sin", 1, "sin(a)"),
        op("sla", 2, "a << b, vacated bits are filled with a copy of the sign bit"),
        op("sle", 2, "a <= b ? 1 : 0"),
        Instruction("sleep", "Pauses execution on the IC for time seconds", listOf(value("time"))),
        op("slez", 1, "a <= 0 ? 1 : 0"),
        op("sll", 2, "a << b"),
        op("slt", 2, "a < b ? 1 : 0"),
        op("sltz", 1, "a < 0 ? 1 : 0"),
        op("sna", 3, "abs(a - b) > max(c * max(abs(a), abs(b)), epsilon * 8) ? 1 : 0"),
        op("snan", 1, "1 if a is NaN, 0 otherwise"),
        op("snanz", 1, "1 if a is not NaN, 0 otherwise"),
        op("snaz", 3, "abs(a) > max(b * abs(a), epsilon * 8) ? 1 : 0"),
        op("sne", 2, "a != b ? 1 : 0"),
        op("snez", 1, "a != 0 ? 1 : 0"),
        op("sqrt", 1, "sqrt(a)"),
        op("sra", 2, "a >> b, vacated bits are filled with a copy of the sign bit"),
        op("srl", 2, "a >> b"),
        Instruction("ss", "Writes value to property of given slot on the provided device", listOf(targetDevice, value("slotIndex"), slotPropertyName, value("value"))),
        op("sub", 2, "a - b"),
        op("tan", 1, "tan(a)"),
        op("trunc", 1, "a with fractional part removed"),
        op("xor", 2, "a ^ b"),
        Instruction("yield", "Pauses execution for 1 tick")
    ).associateBy { it.name }
    fun get(name: String) = operations[name]
}