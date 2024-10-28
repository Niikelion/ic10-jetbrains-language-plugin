package com.niikelion.ic10_language

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.DocMarkup.br
import com.niikelion.ic10_language.DocMarkup.content
import com.niikelion.ic10_language.DocMarkup.div
import com.niikelion.ic10_language.DocMarkup.doc
import com.niikelion.ic10_language.DocMarkup.keyword
import com.niikelion.ic10_language.DocMarkup.text
import com.niikelion.ic10_language.DocMarkup.value
import com.niikelion.ic10_language.psi.*

class Ic10DocumentationProvider: AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return when(element) {
            is Ic10Value -> renderValueDoc(element)
            is Ic10Label -> renderLabelDoc(element)
            is Ic10OperationName -> renderOperationNameDoc(element)
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
        val hash = element.hash

        fun renderOnlyValueDoc(): String? {
            if (hash != null)
                return renderHashDoc(hash)

            val name = element.referenceName

            if (name != null)
                return renderReferenceNameDoc(name)

            return null
        }

        fun renderOnlyParameterDoc(): String? {
            val operationElement = element.findParentOfType<Ic10Operation>()!!

            val operationName = operationElement.operationName.text

            val operation = Instructions.get(operationName) ?: return null

            val argId = operationElement.valueList.indexOf(element)

            if (argId < 0 || argId >= operation.arguments.size) return null

            val arg = operation.arguments[argId]

            return doc(
                keyword(text("parameter ")),
                text(arg.name)
            )
        }

        val texts = arrayOf(renderOnlyParameterDoc(), renderOnlyValueDoc()).filterNotNull()

        return if (texts.isNotEmpty()) texts.joinToString(br().toString()) else null
    }

    private fun renderHashDoc(hash: Ic10Hash): String {
        val value = Ic10PsiUtils.calculateHash(hash)

        return doc(value(text(value.toString())))
    }

    private fun renderReferenceNameDoc(element: Ic10ReferenceName): String? {
        val symbol = Ic10PsiUtils.findAndResolveSymbol(element) ?: return null

        return when (symbol.type) {
            Ic10SymbolType.Constant -> {
                val valueElement = symbol.valueElement

                if (symbol.unresolved) return doc(
                    keyword(text("const")),
                    text(" ${symbol.name}")
                )

                fun getValue(): Pair<String, String?> {
                    if (valueElement is Ic10Label) return Pair(
                        Ic10PsiUtils.getLineNumber(valueElement).toString(),
                        null
                    )
                    if (valueElement is Ic10Value) {
                        val hash = valueElement.hash?.let { Ic10PsiUtils.calculateHash(it).toString() }
                        if (hash != null) return Pair(hash, null)

                        val constant = valueElement.referenceName?.let { Constants.get(it.name!!) }
                        if (constant != null) return Pair(
                            constant.value?.toString() ?: constant.name,
                            constant.description
                        )
                    }

                    return Pair(valueElement.text, null)
                }

                val value = getValue()

                if (value.second != null) return doc(
                    content(
                        keyword(text("const")),
                        text(" ${symbol.name} = "),
                        value(text(value.first))
                    ),
                    content(text(value.second!!))
                )

                doc(
                    keyword(text("const")),
                    text(" ${symbol.name} = "),
                    value(text(value.first))
                )
            }
            Ic10SymbolType.Device -> {
                if (symbol.unresolved) return doc(
                    keyword(text("device ")),
                    text(symbol.name)
                )
                return doc(
                    keyword(text("device ")),
                    text(symbol.name),
                    text(" = "),
                    text(symbol.valueElement.text)
                )
            }
            Ic10SymbolType.Variable -> {
                if (symbol.unresolved) return doc(
                    keyword(text("variable ")),
                    text(symbol.name)
                )

                return doc(
                    keyword(text("variable ")),
                    text(symbol.name),
                    text(" = "),
                    text(symbol.valueElement.text)
                )
            }
        }
    }

    private fun renderOperationNameDoc(element: Ic10OperationName): String? {
        val operation = Instructions.get(element.text) ?: return null

        return doc(
            div(
                keyword(text("operation")),
                text(" "),
                text(operation.name),
                *(operation.arguments.flatMap { listOf(text(" "), text(it.name)) }.toTypedArray())
            ),
            br(),
            text(operation.description)
        )
    }
}

object DocMarkup {
    private val colorScheme get() = EditorColorsManager.getInstance().schemeForCurrentUITheme

    private fun getColor(key: TextAttributesKey) = "#${Integer.toHexString(colorScheme.getAttributes(key).foregroundColor.rgb)}"

    fun doc(vararg children: HtmlChunk): String = StringBuilder().let { sb -> children.forEach { child -> child.appendTo(sb) }; sb.toString() }
    fun content(vararg children: HtmlChunk) = DocumentationMarkup.CONTENT_ELEMENT.children(*children)
    fun div(vararg children: HtmlChunk) = HtmlChunk.div().children(*children)
    fun text(text: String) = HtmlChunk.text(text)
    fun br() = HtmlChunk.br()

    fun value(vararg children: HtmlChunk) = valueTag.children(*children)
    fun keyword(vararg children: HtmlChunk) = keywordTag.children(*children)

    private val valueTag = HtmlChunk.span().attr("style", "color: ${getColor(DefaultLanguageHighlighterColors.NUMBER)};")
    private val keywordTag = HtmlChunk.span().attr("style", "color: ${getColor(DefaultLanguageHighlighterColors.KEYWORD)};")
}