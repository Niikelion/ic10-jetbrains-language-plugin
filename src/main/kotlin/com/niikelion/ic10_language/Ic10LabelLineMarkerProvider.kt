package com.niikelion.ic10_language

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10LabelLineMarkerProvider: LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.elementType == Ic10Types.NAME) {
            val parent = element.findParentOfType<Ic10Label>()

            if (parent != null) {
                return LineMarkerInfo(element, parent.textRange, Ic10Icons.Label, null, null, GutterIconRenderer.Alignment.CENTER) { "$element label" }
            }
        }

        return null
    }
}