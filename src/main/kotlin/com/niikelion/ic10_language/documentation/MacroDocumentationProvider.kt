package com.niikelion.ic10_language.documentation

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.documentation.ui.doc
import com.niikelion.ic10_language.logic.Macros
import com.niikelion.ic10_language.psi.Ic10Macro
import com.niikelion.ic10_language.ui.html.HtmlBuilder

class MacroDocumentationProvider: PsiDocumentationTargetProvider {
    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        if (element !is Ic10Macro) return null

        Macros.resolve(element) ?: return null
        return Ic10RenderableDocumentationTarget(element) {
            HtmlBuilder.build(this.doc)
        }
    }
}