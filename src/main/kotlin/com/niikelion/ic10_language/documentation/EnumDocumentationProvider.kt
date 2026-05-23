package com.niikelion.ic10_language.documentation

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.documentation.ui.doc
import com.niikelion.ic10_language.logic.Enums
import com.niikelion.ic10_language.psi.Ic10Enum
import com.niikelion.ic10_language.psi.Ic10EnumName
import com.niikelion.ic10_language.psi.Ic10EnumProperty
import com.niikelion.ic10_language.ui.html.HtmlBuilder

class EnumDocumentationProvider: PsiDocumentationTargetProvider {
    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        val enum = element.findParentOfType<Ic10Enum>()?.let { Enums.get(it.enumName.text) } ?: return null

        if (element is Ic10EnumName) {
            return Ic10RenderableDocumentationTarget(element) {
                HtmlBuilder.build(enum.doc())
            }
        }
        if (element is Ic10EnumProperty) {
            val prop = enum.values[element.text] ?: return null
            return Ic10RenderableDocumentationTarget(element) {
                HtmlBuilder.build(element.doc(enum, prop))
            }
        }
        return null
    }
}