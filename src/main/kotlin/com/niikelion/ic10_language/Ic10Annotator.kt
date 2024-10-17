package com.niikelion.ic10_language

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.psi.Ic10Line
import com.niikelion.ic10_language.psi.Ic10OperationName
import com.niikelion.ic10_language.psi.Ic10ReferenceName

class Ic10Annotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is Ic10Line -> {
                val document: Document = element.containingFile.viewProvider.document

                val lineNumber = document.getLineNumber(element.textOffset) + 1
                if (lineNumber > 128) {
                    holder
                        .newAnnotation(HighlightSeverity.ERROR, "Line $lineNumber exceeds the 128 limit")
                        .create()
                }
            }
            is Ic10OperationName -> {
                val instruction = Instructions.get(element.text)

                if (instruction == null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unknown instruction").create()
                } else {
                    holder
                        .newAnnotation(HighlightSeverity.INFORMATION, instruction.name)
                        .tooltip(instruction.getTooltipText())
                        .textAttributes(Ic10SyntaxHighlighter.INSTRUCTION).create()
                }
            }
            is Ic10ReferenceName -> {
                val constant = Constants.get(element.text)

                if (constant == null) return

                holder
                    .newAnnotation(HighlightSeverity.INFORMATION, constant.name)
                    .tooltip(constant.getTooltipText())
                    .textAttributes(Ic10SyntaxHighlighter.CONSTANT)
                    .create()
            }
        }
    }
}