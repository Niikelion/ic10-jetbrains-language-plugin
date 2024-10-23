package com.niikelion.ic10_language

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.DocMarkup.value
import com.niikelion.ic10_language.DocMarkup.content
import com.niikelion.ic10_language.DocMarkup.definition
import com.niikelion.ic10_language.DocMarkup.doc
import com.niikelion.ic10_language.DocMarkup.keyword
import com.niikelion.ic10_language.DocMarkup.text
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.Ic10Operation
import com.niikelion.ic10_language.psi.Ic10OperationName
import com.niikelion.ic10_language.psi.Ic10ReferenceName
import com.niikelion.ic10_language.psi.Ic10Value

class Ic10DocumentationProvider: AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return when(element) {
            is Ic10Value -> renderValueDoc(element)
            is Ic10Label -> renderLabelDoc(element)
            is Ic10ReferenceName -> renderVariableReference(element)
            else -> null
        }
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        val value = contextElement?.findParentOfType<Ic10Value>()
        if (value != null) return value

        val opName = contextElement?.findParentOfType<Ic10OperationName>()
        if (opName != null) return opName

        return super.getCustomDocumentationElement(editor, file, contextElement, targetOffset)
    }

    private fun renderLabelDoc(element: Ic10Label): String
    {
        val lineNumber = Ic10PsiUtils.getLineNumber(element)

        return doc(
            keyword(text("label ")),
            text(element.name!!),
            text(" = "),
            value(text(lineNumber.toString())),
        )
    }

    private fun renderValueDoc(element: Ic10Value): String? {
        return "value"
    }

    private fun renderVariableReference(element: Ic10ReferenceName): String? {
        val symbol = Ic10PsiUtils.findAndResolveSymbol(element)

        val operation = element.findParentOfType<Ic10Operation>() ?: return null

        val instruction = Instructions.get(operation.operationName.text) ?: return null

        val argument = instruction.arguments[operation.valueList.indexOf(element.parent)]

        return renderVariableDoc(argument, symbol)
    }

    private fun renderVariableDoc(argument: Instruction.Arg, symbol: Ic10Symbol?): String = doc(
        definition(text(argument.name)),
        content(text(argument.type.name))
    )
}

object DocMarkup {
    fun doc(vararg children: HtmlChunk): String = StringBuilder().let { sb -> children.forEach { child -> child.appendTo(sb) }; sb.toString() }
    fun definition(vararg children: HtmlChunk) = DocumentationMarkup.DEFINITION_ELEMENT.children(*children)
    fun content(vararg children: HtmlChunk) = DocumentationMarkup.CONTENT_ELEMENT.children(*children)
    fun text(text: String) = HtmlChunk.text(text)

    fun value(vararg children: HtmlChunk) = valueTag.children(*children)
    fun keyword(vararg children: HtmlChunk) = keywordTag.children(*children)

    private val valueTag = HtmlChunk.span().attr("style", "color: #54a4ef;")
    private val keywordTag = HtmlChunk.span().attr("style", "color: #cb8b6b;")
}