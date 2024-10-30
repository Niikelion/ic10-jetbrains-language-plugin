package com.niikelion.ic10_language

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

class Ic10Reference(element: PsiElement, textRange: TextRange): PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {
    val text: String = element.text

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val declarations = Ic10PsiUtils.findDeclarations(myElement.containingFile, myElement.text)

        return declarations.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val elem = element

        if (elem is PsiNamedElement) {
            elem.setName(newElementName)
            return elem
        } else {
            return super.handleElementRename(newElementName)
        }
    }
}