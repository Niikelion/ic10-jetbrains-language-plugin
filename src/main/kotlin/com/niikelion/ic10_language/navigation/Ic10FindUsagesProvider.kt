package com.niikelion.ic10_language.navigation

import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.Ic10PsiUtils

class Ic10FindUsagesProvider: FindUsagesProvider {
    override fun canFindUsagesFor(psiElement: PsiElement): Boolean = Ic10PsiUtils.isDeclaration(psiElement)

    override fun getHelpId(psiElement: PsiElement): String? {
        TODO("Not yet implemented")
    }

    override fun getType(element: PsiElement): String {
        TODO("Not yet implemented")
    }

    override fun getDescriptiveName(element: PsiElement): String {
        TODO("Not yet implemented")
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        TODO("Not yet implemented")
    }
}