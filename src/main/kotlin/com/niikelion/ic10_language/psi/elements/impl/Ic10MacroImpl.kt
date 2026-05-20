package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.logic.Macro
import com.niikelion.ic10_language.logic.Macros
import com.niikelion.ic10_language.logic.NumberValue
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement

private val macroRegex = Regex("""(?<name>[A-Z]+)\("(?<value>.*)"\)""")

open class Ic10MacroImpl(node: ASTNode): ASTWrapperPsiElement(node), Ic10ValueLikeElement {
    override fun getValue(): IUnresolvedValue? {
        val groups = macroRegex.matchEntire(node.text)?.groups ?: return null
        val name = groups["name"]?.value ?: return null
        val value = groups["value"]?.value ?: return null
        return when (val result = Macros.get(name)?.parse(value)) {
            is Macro.ParseResult.Success -> NumberValue(result.value.toDouble())
            else -> null
        }
    }
}