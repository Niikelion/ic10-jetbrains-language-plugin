package com.niikelion.ic10_language

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class Ic10ReferenceContributor: PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val pattern = psiElement()
        registrar.registerReferenceProvider(pattern, object: PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                return arrayOf(Ic10LabelReference(element, element.textRange))
            }
        })
    }
}