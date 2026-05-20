package com.niikelion.ic10_language.ui.swing

import com.intellij.util.ui.components.BorderLayoutPanel
import com.niikelion.ic10_language.utils.StructuralDSL
import kotlinx.collections.immutable.toImmutableList
import javax.swing.JComponent

typealias Content = SwingBuilder.() -> Unit

class SwingBuilder: StructuralDSL<JComponent>() {
    companion object {
        fun build(content: Content) = SwingBuilder().apply(content).elements.toImmutableList()
        fun panel(content: Content) = build(content).let{ components ->
            BorderLayoutPanel().apply {
                components.forEach(this::add)
            }
        }
    }

    fun <T: JComponent>element(content: Content, builder: SwingBuilder.(children: List<JComponent>) -> T) =
        builder(build(content)).also(this::insert)

    fun <T: JComponent>element(builder: SwingBuilder.() -> T) = builder().also(this::insert)
}