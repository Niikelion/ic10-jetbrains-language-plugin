package com.niikelion.ic10_language

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.psi.impl.Ic10BinOpNameImpl
import com.niikelion.ic10_language.psi.impl.Ic10UnOpNameImpl

class Ic10Annotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is Ic10BinOpNameImpl, is Ic10UnOpNameImpl -> holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element).textAttributes(Ic10SyntaxHighlighter.INSTRUCTION).create()
        }
    }
}