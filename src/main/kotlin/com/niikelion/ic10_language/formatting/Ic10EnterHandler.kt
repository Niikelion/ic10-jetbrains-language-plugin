package com.niikelion.ic10_language.formatting

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.parentOfType
import com.niikelion.ic10_language.psi.Ic10Line

class Ic10EnterHandler: EnterHandlerDelegate {
    override fun postProcessEnter(
        file: PsiFile,
        editor: Editor,
        dataContext: DataContext
    ): EnterHandlerDelegate.Result {
        handleEnter(file, editor)
        return EnterHandlerDelegate.Result.Continue
    }

    private fun handleEnter(
        file: PsiFile,
        editor: Editor
    ) {
        val currentLine = editor.document.getLineNumber(editor.caretModel.offset)
        if (currentLine == -1) return

        val prevLineStart = editor.document.getLineStartOffset(currentLine - 1)
        val prevLineEnd = editor.document.getLineEndOffset(currentLine - 1)

        if (prevLineStart == prevLineEnd) return

        val element = file.findElementAt(prevLineEnd - 1) ?: return

        val lineNode = element.parentOfType<Ic10Line>() ?: return
        if (lineNode.label != null)
            CodeStyleManager.getInstance(file.project).adjustLineIndent(file, lineNode.textRange)
    }
}