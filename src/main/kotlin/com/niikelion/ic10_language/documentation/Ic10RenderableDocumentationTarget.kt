package com.niikelion.ic10_language.documentation

import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.createSmartPointer

@Suppress("UnstableApiUsage")
class Ic10RenderableDocumentationTarget<T: PsiElement>(
    val element: T,
    private val render: T.() -> String
): DocumentationTarget {
    override fun computePresentation(): TargetPresentation = TargetPresentation.builder(element.text)
        .presentation()

    override fun createPointer(): Pointer<out DocumentationTarget> {
        val elementPtr = element.createSmartPointer()

        return Pointer {
            val element = elementPtr.dereference() ?: return@Pointer null
            Ic10RenderableDocumentationTarget(element, render)
        }
    }

    override fun computeDocumentation(): DocumentationResult =
        DocumentationResult.documentation(renderDocumentation())

    private fun renderDocumentation(): String = element.render()
}