package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.logic.NumberValue
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement

private val decimalRegex = Regex("""(?<value>-?[0-9]+)""")
private val binaryRegex = Regex("""%(?<value>[0-1](_?[0-1])*)""")
private val hexadecimalRegex = Regex("""\$(?<value>[0-9A-F](_?[0-9A-F])*)""")
private val floatRegex = Regex("""(?<value>-?[0-9]+\.[0-9]+)""")

class Format(val regex: Regex, val extractor: (str: String) -> Double?)

private val formats = arrayOf(
    Format(decimalRegex) { it.toIntOrNull(10)?.toDouble() },
    Format(binaryRegex) { it.toIntOrNull(2)?.toDouble() },
    Format(hexadecimalRegex) { it.toIntOrNull(16)?.toDouble() },
    Format(floatRegex) { it.toDoubleOrNull() }
)

open class Ic10NumberImpl(node: ASTNode): ASTWrapperPsiElement(node), Ic10ValueLikeElement {
    override fun getValue(): IUnresolvedValue? = formats.firstNotNullOfOrNull { format ->
        val str = format.regex.matchEntire(text)?.groups?.get("value")?.value?.replace("_", "")
        str?.let { format.extractor(it) }
    }?.let { NumberValue(it) }
}