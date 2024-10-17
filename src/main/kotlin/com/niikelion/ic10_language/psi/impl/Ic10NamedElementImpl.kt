package com.niikelion.ic10_language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.niikelion.ic10_language.psi.Ic10ElementFactory
import com.niikelion.ic10_language.psi.Ic10NamedElement
import com.niikelion.ic10_language.psi.Ic10Types

open class Ic10NamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), Ic10NamedElement {
    override fun setName(name: String): PsiElement {
        val oldTextNode = nameIdentifier?.node ?: return this
        val newTextNode = Ic10ElementFactory.createLabel(project, name)?.nameIdentifier?.node ?: return this
        oldTextNode.treeParent.replaceChild(oldTextNode, newTextNode)

        return this
    }

    override fun getName(): String? = nameIdentifier?.text

    override fun getNameIdentifier(): PsiElement? = PsiTreeUtil.collectElements(this) { it.elementType == Ic10Types.NAME }.firstOrNull()

    override fun getReferences(): Array<PsiReference> =
        ReferenceProvidersRegistry.getReferencesFromProviders(this)
}