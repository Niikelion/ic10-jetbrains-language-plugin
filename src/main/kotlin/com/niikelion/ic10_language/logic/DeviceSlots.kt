package com.niikelion.ic10_language.logic

class DeviceSlot(val name: String)

class DeviceSlots {
    companion object {
        private val referenceRegex = Regex("""d(?<source>r+\d+)""")

        val db = DeviceSlot("db")
        val numberedDeviceSlots = Array(6) { DeviceSlot("d$it") }
        val all = arrayOf(db, *numberedDeviceSlots)

        fun get(n: Int) = if (n < 0 || n >= numberedDeviceSlots.size) null else numberedDeviceSlots[n]
        fun get(name: String) = all.find { it.name == name }

        fun isValidName(str: String) = all.firstOrNull { it.name == str } != null
        fun isValidReference(str: String) = referenceRegex.matchEntire(str)?.let { it.groups["source"]?.value }?.let { Registers.isValidReference(it) } ?: false

        fun parseReference(str: String) = referenceRegex.matchEntire(str)
            ?.let { it.groups["source"]?.value }
            ?.let { Registers.parseReference(it) }
    }
}