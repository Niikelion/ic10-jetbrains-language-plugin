package com.niikelion.ic10_language

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10Operation
import com.niikelion.ic10_language.psi.Ic10ReferenceName

object Ic10PsiUtils {
    private fun findLabelsInFile(file: PsiFile): Collection<Ic10Label> = PsiTreeUtil.findChildrenOfType(file, Ic10Label::class.java)
    fun findLabelsInFile(file: PsiFile, name: String) = findLabelsInFile(file).filter { it.name == name }

    private fun findReferencesInFile(file: PsiFile) = PsiTreeUtil.findChildrenOfType(file, Ic10ReferenceName::class.java)
    fun findReferencesInFile(file: PsiFile, name: String) = findReferencesInFile(file).filter { it.name == name }

    private fun getDeclaredName(element: Ic10Operation): String? {
        val opName = element.operationName.text
        if (opName != "define" && opName != "alias") return null

        val value = element.valueList.firstOrNull() ?: return null

        val reference = value.referenceName ?: return null

        return reference.name
    }

    fun findDeclarations(file: PsiFile, name: String): Collection<PsiElement> {
        val labels = findLabelsInFile(file, name)
        val aliases = PsiTreeUtil.findChildrenOfType(file, Ic10Operation::class.java).filter { getDeclaredName(it) == name }.map { it.valueList.first().referenceName!! }

        return labels + aliases
    }

    fun isValidRegister(element: Ic10ReferenceName) = registers.contains(element.name)
    fun isValidDevice(element: Ic10ReferenceName) = devices.contains(element.name)

    private val registers = (List(18) { "r$it" } + listOf("sp", "ra")).toSet()
    private val devices = (List(6) { "d$it" } + listOf("db")).toSet()
}