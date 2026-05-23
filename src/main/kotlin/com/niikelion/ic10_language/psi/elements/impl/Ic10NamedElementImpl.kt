package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.ide.IconProvider
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.niikelion.ic10_language.Ic10Icons
import com.niikelion.ic10_language.psi.*
import com.niikelion.ic10_language.psi.elements.Ic10NamedElement
import javax.swing.Icon

class Ic10NamedElementIconProvider: IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? = when (element) {
        is Ic10Label -> Ic10Icons.Constant
        is Ic10ReferenceName -> Ic10Icons.Variable
        else -> null
    }
}

abstract class Ic10NamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), Ic10NamedElement {
    override fun setName(name: String): PsiElement {
        val oldTextNode = nameIdentifier.node ?: return this
        val newTextNode = Ic10ElementFactory.createLabel(project, name)?.nameIdentifier?.node ?: return this
        oldTextNode.treeParent.replaceChild(oldTextNode, newTextNode)

        return this
    }

    override fun getName(): String = nameIdentifier.text

    override fun getNameIdentifier(): PsiElement = PsiTreeUtil.collectElements(this) { it.elementType == Ic10Types.NAME }.first()

    override fun getReferences() =
        ReferenceProvidersRegistry.getReferencesFromProviders(this)

    override fun getPresentation(): ItemPresentation? =
        NamedElementPresentation(this)

    override fun getUseScope() = GlobalSearchScope.fileScope(containingFile)
}