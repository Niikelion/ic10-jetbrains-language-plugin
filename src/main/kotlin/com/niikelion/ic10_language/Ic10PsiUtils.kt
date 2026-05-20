package com.niikelion.ic10_language

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.niikelion.ic10_language.logic.DeviceSlots
import com.niikelion.ic10_language.logic.Registers
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10Operation
import com.niikelion.ic10_language.psi.elements.Ic10NamedElement

object Ic10PsiUtils {
    fun findLabelsInFile(file: PsiFile): Collection<Ic10Label> = PsiTreeUtil.findChildrenOfType(file, Ic10Label::class.java)
    fun findLabelsInFile(file: PsiFile, name: String) = findLabelsInFile(file).filter { it.name == name }

    fun findDeclarations(file: PsiFile): Collection<Ic10NamedElement> {
        val labels = findLabelsInFile(file)
        val aliases = PsiTreeUtil
            .findChildrenOfType(file, Ic10Operation::class.java)
            .filter { it.declaredName != null }
            .map { it.valueList.first().referenceName!! }

        return labels + aliases
    }

    fun findDeclarations(file: PsiFile, name: String): Collection<Ic10NamedElement> {
        val labels = findLabelsInFile(file, name)
        val aliases =
            PsiTreeUtil.findChildrenOfType(file, Ic10Operation::class.java).filter { it.declaredName == name }
                .map { it.valueList.first().referenceName!! }

        return labels + aliases
    }

    fun isValidRegister(element: Ic10NamedElement) = Registers.isValidName(element.name) || Registers.isValidReference(element.name)
    fun isValidDevice(element: Ic10NamedElement) = DeviceSlots.isValidName(element.name) || DeviceSlots.isValidReference(element.name)

    fun getLineNumber(element: PsiElement): Int = element.containingFile.viewProvider.document.getLineNumber(element.textOffset) + 1
}

fun <T> PsiElement.cache(provider: CachedValueProvider<T>): CachedValue<T> =
    CachedValuesManager.getManager(project).createCachedValue(provider)

fun <T> PsiElement.dependentOnThis(value: T) = CachedValueProvider.Result.create(value, this)