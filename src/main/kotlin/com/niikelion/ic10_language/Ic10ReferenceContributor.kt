package com.niikelion.ic10_language

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10ReferenceContributor: PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val referencePattern = psiElement(Ic10Types.REFERENCE_NAME)
        registrar.registerReferenceProvider(referencePattern, object: PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> =
                arrayOf(Ic10Reference(element, TextRange(0, element.textLength)))
        })
    }
}