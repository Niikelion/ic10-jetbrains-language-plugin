package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.niikelion.ic10_language.logic.Enums
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.psi.Ic10EnumName
import com.niikelion.ic10_language.psi.Ic10EnumProperty
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement

open class Ic10EnumImpl(node: ASTNode): ASTWrapperPsiElement(node), Ic10ValueLikeElement {
    override fun getValue(): IUnresolvedValue? {
        val name = findChildByClass(Ic10EnumName::class.java)?.text ?: return null
        val property = findChildByClass(Ic10EnumProperty::class.java)?.text ?: return null

        return Enums.get(name)?.values?.get(property)?.getValue()
    }
}