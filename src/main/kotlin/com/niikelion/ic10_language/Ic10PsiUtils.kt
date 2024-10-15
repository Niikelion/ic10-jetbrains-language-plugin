package com.niikelion.ic10_language

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.niikelion.ic10_language.psi.impl.Ic10LabelNameImpl

object Ic10PsiUtils {
    fun resolveLabels(origin: PsiElement): List<PsiElement> {
        val name = origin.text
        val targets = PsiTreeUtil.findChildrenOfType(origin.containingFile, Ic10LabelNameImpl::class.java)

        return targets.filter { it.text == name }
    }
}