package com.niikelion.ic10_language.ui.swing.layout

import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBScrollPane
import com.niikelion.ic10_language.ui.swing.Content
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import javax.swing.BorderFactory
import javax.swing.ScrollPaneConstants

enum class Scroll {
    NONE {
        override fun asVertical(): Int = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
        override fun asHorizontal(): Int = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    },
    AUTO {
        override fun asVertical(): Int = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        override fun asHorizontal(): Int = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
    },
    FORCE {
        override fun asVertical(): Int = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        override fun asHorizontal(): Int = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
    };

    abstract fun asVertical(): Int
    abstract fun asHorizontal(): Int
}

fun SwingBuilder.scrollable(verticalScroll: Scroll, horizontalScroll: Scroll, content: Content) = element(content) { elements ->
    val first = elements.firstOrNull()
    val pane = first?.let { ScrollPaneFactory.createScrollPane(
        it,
        verticalScroll.asVertical(),
        horizontalScroll.asHorizontal())
    } ?: JBScrollPane()
    pane.apply {
        border = BorderFactory.createEmptyBorder()
    }
}

fun SwingBuilder.scrollable(verticalScroll: Scroll, content: Content) = scrollable(verticalScroll, Scroll.NONE, content)
fun SwingBuilder.scrollable(content: Content) = scrollable(Scroll.AUTO, content)