package com.niikelion.ic10_language.psi.elements

import com.intellij.psi.PsiNameIdentifierOwner

interface Ic10NamedElement: PsiNameIdentifierOwner {
    override fun getName(): String
}