package com.niikelion.ic10_language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.Ic10LabelReference
import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.psi.Ic10ElementFactory
import com.niikelion.ic10_language.psi.Ic10JumpTarget
import com.niikelion.ic10_language.psi.Ic10NamedElement
import com.niikelion.ic10_language.psi.Ic10Types

open class Ic10NamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), Ic10NamedElement {
    override fun setName(name: String): PsiElement {
        val textNode = node.findChildByType(Ic10Types.NAME) ?: return this

        val newLabel = Ic10ElementFactory.createLabel(project, name) ?: return this

        node.replaceChild(textNode, newLabel.labelName.firstChild.node)

        return this
    }

    override fun getName(): String? = nameIdentifier?.text

    override fun getNameIdentifier(): PsiElement? = node.findChildByType(Ic10Types.NAME)?.psi

    override fun canNavigateToSource(): Boolean {
        return true
    }

    override fun getReference(): PsiPolyVariantReference = Ic10LabelReference(this, textRange)
}