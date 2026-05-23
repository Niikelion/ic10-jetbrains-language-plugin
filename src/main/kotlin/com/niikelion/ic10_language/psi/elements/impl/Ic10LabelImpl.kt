package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.lang.ASTNode
import com.niikelion.ic10_language.logic.Entity
import com.niikelion.ic10_language.logic.NumberValue
import com.niikelion.ic10_language.psi.elements.Ic10LabelElement

open class Ic10LabelImpl(node: ASTNode) : Ic10NamedElementImpl(node), Ic10LabelElement {
    override fun getValue() = NumberValue(getLineNumber().toDouble())

    override fun getLineNumber() = containingFile.viewProvider.document.getLineNumber(textOffset)

    override val entity get() = Entity.from(this)
}