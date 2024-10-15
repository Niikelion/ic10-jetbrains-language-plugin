package com.niikelion.ic10_language

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.psi.Ic10Line

class Ic10Annotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is Ic10Line) return

        val document: Document = element.containingFile.viewProvider.document

        val lineNumber = document.getLineNumber(element.textOffset) + 1
        if (lineNumber > 128) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, "Line $lineNumber exceeds the 128 limit")
                .create()
        }
    }
}