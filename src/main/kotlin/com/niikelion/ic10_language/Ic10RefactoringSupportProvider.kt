package com.niikelion.ic10_language

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.psi.Ic10NamedElement

class Ic10RefactoringSupportProvider: RefactoringSupportProvider() {
    override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
        element is Ic10NamedElement
}