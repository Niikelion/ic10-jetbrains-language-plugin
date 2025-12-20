package com.niikelion.ic10_language.ui.html

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.openapi.util.text.HtmlChunk.*
import com.intellij.ui.ColorUtil
import com.niikelion.ic10_language.annotations.Ic10SyntaxHighlighter
import java.awt.Color

typealias Content = HtmlBuilder.() -> Unit
typealias Properties = Element.() -> Element

fun Element.css(vararg props: String): Element = attr("style", "${props.joinToString(";")};")

class HtmlBuilder {
    data class Element(private val builder: HtmlBuilder, private val sourceChunk: HtmlChunk.Element) {
        operator fun invoke(content: Content) = HtmlBuilder()
            .apply(content).children
            .let(sourceChunk::children)
            .apply(builder::append)

        operator fun invoke(properties: Properties, content: Content) = HtmlBuilder()
            .apply(content).children
            .let(sourceChunk::children)
            .let(properties)
            .apply(builder::append)
    }

    companion object {
        private val colorScheme get() = EditorColorsManager.getInstance().schemeForCurrentUITheme

        fun build(content: HtmlBuilder.() -> Unit): String =
            HtmlBuilder().apply(content).children.joinToString("\n")

        fun getColorValue(key: TextAttributesKey): Color
                = colorScheme.getAttributes(key, true).foregroundColor

        fun namedColor(key: TextAttributesKey)
                = ColorUtil.toHtmlColor(getColorValue(key))

        fun tokenKeyCss(key: TextAttributesKey) = "color: ${namedColor(key)}"
    }

    private val children = mutableListOf<HtmlChunk>()

    fun append(chunk: HtmlChunk) {
        children.add(chunk)
    }

    fun render(content: Content) {
        this.content()
    }

    fun element(factory: (children: List<HtmlChunk>) -> HtmlChunk, content: Content) {
        HtmlBuilder().apply(content).children.let(factory).apply(this::append)
    }

    val span = Element(this, span())
    fun br() = append(HtmlChunk.br())
    fun text(text: String) = append(HtmlChunk.text(text))
    fun separated(separator: Content, content: Content) {
        val separator = HtmlBuilder().apply(separator).children.let{ if (it.isNotEmpty()) it.last() else null }
        if (separator == null) return render(content)
        val elements = HtmlBuilder().apply(content).children
        elements.flatMap { listOf(it, separator) }.dropLast(1).forEach(this::append)
    }
    fun separated(separator: String, content: Content) {
        val elements = HtmlBuilder().apply(content).children
        elements.flatMap { listOf(it, HtmlChunk.text(separator)) }.dropLast(1).forEach(this::append)
    }
    fun number(text: String) = span({
        css(tokenKeyCss(Ic10SyntaxHighlighter.NUMBER))
    }) { text(text) }
    fun string(text: String) = span({
        css(tokenKeyCss(Ic10SyntaxHighlighter.MACRO))
    }) { text(text) }
    fun keyword(text: String) = span({
        css(tokenKeyCss(Ic10SyntaxHighlighter.INSTRUCTION))
    }) { text(text) }
    fun operator(text: String) = span({
        css(tokenKeyCss(DefaultLanguageHighlighterColors.OPERATION_SIGN))
    }) { text(text) }
    fun global(text: String) = span({
        css(tokenKeyCss(DefaultLanguageHighlighterColors.GLOBAL_VARIABLE))
    }) { text(text) }
    fun local(text: String) = span({
        css(tokenKeyCss(DefaultLanguageHighlighterColors.INSTANCE_METHOD))
    }) { text(text) }
}