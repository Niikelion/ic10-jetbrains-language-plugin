package com.niikelion.ic10_language.documentation

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.documentation.ui.doc
import com.niikelion.ic10_language.psi.Ic10Value
import com.niikelion.ic10_language.ui.html.HtmlBuilder

class SymbolDocumentationProvider: PsiDocumentationTargetProvider {
    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        if (element !is Ic10Value) return null

        val reference = element.referenceName ?: element.channel?.referenceName ?: return null

        val symbol = Ic10PsiUtils.findAndResolveSymbol(reference) ?: return null
        if (symbol.unresolved) return null

        return Ic10RenderableDocumentationTarget(reference) {
            HtmlBuilder.build(symbol.doc)
        }
    }
}