package com.niikelion.ic10_language

import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10ReferenceName

object Ic10PsiUtils {
    fun findLabelsInFile(file: PsiFile): Collection<Ic10Label> = PsiTreeUtil.findChildrenOfType(file, Ic10Label::class.java)
    fun findLabelsInFile(file: PsiFile, name: String) = findLabelsInFile(file).filter { it.name == name }

    fun findReferencesInFile(file: PsiFile) = PsiTreeUtil.findChildrenOfType(file, Ic10ReferenceName::class.java)
    fun findReferencesInFile(file: PsiFile, name: String) = findReferencesInFile(file).filter { it.name == name }
}