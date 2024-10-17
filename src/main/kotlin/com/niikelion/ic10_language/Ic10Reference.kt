package com.niikelion.ic10_language

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

class Ic10Reference(element: PsiElement, textRange: TextRange): PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {
    val text: String = element.text

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val labels = Ic10PsiUtils.findLabelsInFile(myElement.containingFile, myElement.text)
        val references = Ic10PsiUtils.findReferencesInFile(myElement.containingFile, myElement.text)

        return (labels + references).map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun getVariants(): Array<out Any> {
        val labels = Ic10PsiUtils.findLabelsInFile(myElement.containingFile, myElement.text)
        val references = Ic10PsiUtils.findLabelsInFile(myElement.containingFile, myElement.text)

        return (labels + references).map { LookupElementBuilder.create(it).withIcon(Ic10Icons.Label).withTypeText("a") }.toTypedArray()
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