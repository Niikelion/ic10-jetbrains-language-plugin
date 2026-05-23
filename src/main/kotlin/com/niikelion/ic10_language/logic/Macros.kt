package com.niikelion.ic10_language.logic

import com.niikelion.ic10_language.psi.Ic10Macro
import java.util.zip.CRC32
import kotlin.code

data class Macro(
    val name: String,
    val description: String,
    val parser: (value: String) -> ParseResult
) {
    sealed class ParseResult {
        data class Success(val value: Long): ParseResult()
        data class Failure(val error: String): ParseResult()

        val uncheckedValue get(): Long? = when (this) {
            is Success -> value
            is Failure -> null
        }
    }

    fun parse(value: String): ParseResult = parser(value)
}

object Macros {
    val hash = Macro("HASH", "Converts given string to numeric hash") { value ->
        CRC32().apply { update(value.toByteArray()) }.value.let(Macro.ParseResult::Success)
    }
    private val macros = arrayOf(
        hash,
        Macro("STR", "Packs up to 5 characters of given string into an integer") { value ->
            if (value.length > 5) Macro.ParseResult.Failure("Text exceeds 5 characters")
            else value.map { it.code.toLong() }.reduce { acc, i -> (acc shl 8) or i }.let(Macro.ParseResult::Success)
        }
    ).associateBy(Macro::name)

    fun resolve(source: Ic10Macro): Macro? = macros[source.name]
    fun get(name: String) = macros[name]
}

val Ic10Macro.name get(): String = macroName.text.substringBeforeLast("(")
val Ic10Macro.valueText get(): String = macroValue.text.let { it.substring(1, it.length - 1) }
fun Ic10Macro.parse() = Macros.resolve(this)?.parse(this.valueText)
val Ic10Macro.intValue get() = when (val result = parse()) {
    is Macro.ParseResult.Success -> result.value
    else -> null
}