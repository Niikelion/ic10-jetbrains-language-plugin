package com.niikelion.ic10_language.documentation

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.documentation.ui.doc
import com.niikelion.ic10_language.logic.DeviceReferenceValue
import com.niikelion.ic10_language.logic.Entity
import com.niikelion.ic10_language.logic.RegisterReferenceValue
import com.niikelion.ic10_language.psi.Ic10File
import com.niikelion.ic10_language.psi.Ic10ReferenceName
import com.niikelion.ic10_language.ui.html.HtmlBuilder

class ReferenceNameDocumentationProvider: PsiDocumentationTargetProvider {
    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        if (element !is Ic10ReferenceName) return null

        val file = element.containingFile
        if (file !is Ic10File) return null

        val name = element.name

        val entity = file.findEntity(name)
            ?: RegisterReferenceValue.fromString(name)?.let { Entity.builtin(name, it) }
            ?: DeviceReferenceValue.fromString(name)?.let { Entity.builtin(name, it) }
            ?: return null
        if (!entity.resolved) return null

        return Ic10RenderableDocumentationTarget(element) {
            HtmlBuilder.build(entity.doc)
        }
    }
}