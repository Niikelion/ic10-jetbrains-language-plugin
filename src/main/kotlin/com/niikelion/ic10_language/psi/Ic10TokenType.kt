package com.niikelion.ic10_language.psi

import com.intellij.psi.tree.IElementType
import com.niikelion.ic10_language.Ic10Language

class Ic10TokenType(debugName: String): IElementType(debugName, Ic10Language.Instance) {

    override fun toString() = "Ic10Tokens." + super.toString()
}