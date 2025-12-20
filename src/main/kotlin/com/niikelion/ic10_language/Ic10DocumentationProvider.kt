package com.niikelion.ic10_language

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10DocumentationProvider: AbstractDocumentationProvider() {
    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        return contextElement?.findParent { when(elementType) {
            Ic10Types.MACRO, Ic10Types.VALUE, Ic10Types.OPERATION_NAME -> true
            else -> false
        } } ?: super.getCustomDocumentationElement(editor, file, contextElement, targetOffset)
    }
}

fun PsiElement.findParent(condition: PsiElement.() -> Boolean): PsiElement? =
    if (parent?.condition() == true) parent else parent?.findParent(condition)