package com.niikelion.ic10_language

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10LabelReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    val text: String = element.text

    override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.element

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
        Ic10PsiUtils.resolveLabels(myElement).map { PsiElementResolveResult(it) }.toTypedArray()

    override fun getVariants(): Array<String> = multiResolve(false).mapNotNull {
        it.element?.node?.findChildByType(
            Ic10Types.NAME
        )?.text
    }.toTypedArray()
}