package com.niikelion.ic10_language.annotations

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.Ic10Icons
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10DefinitionLineMarkerProvider: LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.elementType != Ic10Types.NAME) return null

        val label = element.findParentOfType<Ic10Label>() ?: return null

        return LineMarkerInfo(
            element,
            label.textRange,
            Ic10Icons.Label,
            null,
            null,
            GutterIconRenderer.Alignment.CENTER
        ) { "$element label" }
    }
}