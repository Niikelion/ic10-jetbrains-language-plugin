package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement

open class Ic10ChannelImpl(node: ASTNode): ASTWrapperPsiElement(node), Ic10ValueLikeElement {
    override fun getValue(): IUnresolvedValue? = TODO("channels not supported")
}