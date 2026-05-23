package com.niikelion.ic10_language.navigation

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.niikelion.ic10_language.Ic10LexerAdapter
import com.niikelion.ic10_language.logic.IEntitySource
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10ReferenceName
import com.niikelion.ic10_language.psi.Ic10TokenSets
import com.niikelion.ic10_language.psi.elements.Ic10NamedElement

class Ic10FindUsagesProvider: FindUsagesProvider {
    override fun canFindUsagesFor(psiElement: PsiElement): Boolean =
        psiElement is IEntitySource && psiElement.entity != null

    override fun getWordsScanner(): WordsScanner =
        DefaultWordsScanner(Ic10LexerAdapter(), Ic10TokenSets.NAMES, Ic10TokenSets.COMMENTS, TokenSet.EMPTY)

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement): String = when (element) {
        is Ic10Label -> "label"
        is Ic10ReferenceName -> "variable"
        else -> ""
    }

    override fun getDescriptiveName(element: PsiElement) = getNodeText(element, false)

    override fun getNodeText(element: PsiElement, useFullName: Boolean) =
        if (element is Ic10NamedElement) element.name else ""
}