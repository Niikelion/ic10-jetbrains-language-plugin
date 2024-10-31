package com.niikelion.ic10_language.navigation

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.niikelion.ic10_language.Ic10LexerAdapter
import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10ReferenceName
import com.niikelion.ic10_language.psi.Ic10TokenSets

class Ic10FindUsagesProvider: FindUsagesProvider {
    override fun canFindUsagesFor(psiElement: PsiElement): Boolean = Ic10PsiUtils.isDeclaration(psiElement)

    override fun getWordsScanner(): WordsScanner =
        DefaultWordsScanner(Ic10LexerAdapter(), Ic10TokenSets.NAMES, Ic10TokenSets.COMMENTS, TokenSet.EMPTY)

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement): String = when (element) {
        is Ic10Label -> "label"
        is Ic10ReferenceName -> "variable"
        else -> ""
    }

    override fun getDescriptiveName(element: PsiElement): String = when (element) {
        is Ic10Label -> element.name!!
        is Ic10ReferenceName -> element.name!!
        else -> ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return when (element) {
            is Ic10Label -> element.name!!
            is Ic10ReferenceName -> element.name!!
            else -> ""
        }
    }
}