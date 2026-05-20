package com.niikelion.ic10_language.logic

class Register(val name: String)

class Registers {
    companion object {
        private val referenceRegex = Regex("""r*(?<source>r\d+)""")

        val all = Array(18) { Register("r$it") }
        val ra = get(16)!!
        val sp = get(17)!!
        val aliases = mapOf(Pair("ra", ra), Pair("sp", sp))

        fun aliasesFor(register: Register) = aliases.filter { register == it.value }.map { it.key }

        fun isValidName(str: String) = aliases.containsKey(str) || all.firstOrNull { it.name == str } != null
        fun isValidReference(str: String) = referenceRegex.matchEntire(str)?.let { it.groups["source"]?.value }?.let { isValidName(it) } ?: false

        fun get(n: Int) = if (n < 0 || n >= all.size) null else all.elementAtOrNull(n)
        fun get(name: String) = all.find { it.name == name }

        fun parseReference(str: String) = referenceRegex.matchEntire(str)
            ?.let { it.groups["source"]?.value }
            ?.let { get(it) }
            ?.let { Pair(it, str.count { c -> c == 'r' } - 1) }
    }
}