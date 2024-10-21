package com.niikelion.ic10_language

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.DocMarkup.content
import com.niikelion.ic10_language.DocMarkup.definition
import com.niikelion.ic10_language.DocMarkup.doc
import com.niikelion.ic10_language.DocMarkup.text
import com.niikelion.ic10_language.psi.Ic10Operation
import com.niikelion.ic10_language.psi.Ic10ReferenceName

class Ic10DocumentationProvider: AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return when(element) {
            is Ic10ReferenceName -> renderVariableReference(element)
            is Ic10Operation -> Instructions.get(element.operationName.text)?.let { renderOperationDoc(it) }
            else -> null
        }
    }

    private fun renderVariableReference(element: Ic10ReferenceName): String? {
        val operation = element.findParentOfType<Ic10Operation>() ?: return null

        val instruction = Instructions.get(operation.operationName.text) ?: return null

        val argument = instruction.arguments[operation.valueList.indexOf(element.parent)]

        return renderVariableDoc(argument)
    }

    private fun renderVariableDoc(argument: Instruction.Arg): String = doc(
        definition(text(argument.name)),
        content(text(argument.type.name))
    )

    private fun renderOperationDoc(instruction: Instruction): String = doc(
        definition(text("Operation")),
        content(text(instruction.name)),
    )
}

object DocMarkup {
    fun doc(vararg children: HtmlChunk): String = StringBuilder().let { sb -> children.forEach { child -> child.appendTo(sb) }; sb.toString() }
    fun definition(vararg children: HtmlChunk) = DocumentationMarkup.DEFINITION_ELEMENT.children(*children)
    fun content(vararg children: HtmlChunk) = DocumentationMarkup.CONTENT_ELEMENT.children(*children)
    fun text(text: String) = HtmlChunk.text(text)

    private fun blue(vararg children: HtmlChunk) = colorTag.children(*children)

    private val colorTag = HtmlChunk.span().attr("color", "blueberry")
}