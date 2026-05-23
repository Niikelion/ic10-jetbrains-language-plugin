package com.niikelion.ic10_language.annotations

import com.intellij.codeInsight.hints.HintInfo
import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import com.niikelion.ic10_language.Ic10Language
import com.niikelion.ic10_language.psi.Ic10Operation

@Suppress("UnstableApiUsage")
class Ic10InlayParameterHintsProvider: InlayParameterHintsProvider {
    override fun getDefaultBlackList(): Set<String?> = setOf()

    override fun getHintInfo(element: PsiElement): HintInfo? {
        if (element !is Ic10Operation) return null

        val instruction = element.instruction ?: return null
        return HintInfo.MethodInfo(instruction.name, instruction.arguments.map { it.name })
    }

    override fun getParameterHints(element: PsiElement): List<InlayInfo?> {
        if (element !is Ic10Operation) return emptyList()
        val instruction = element.instruction ?: return emptyList()
        return element.valueList.mapIndexedNotNull { index, value ->
            if (index >= instruction.arguments.size) null
            else InlayInfo("${instruction.arguments[index].name}:", value.startOffset)
        }
    }

    override fun getInlayPresentation(inlayText: String): String = inlayText
    override fun getBlackListDependencyLanguage(): Language = Ic10Language.Instance
}