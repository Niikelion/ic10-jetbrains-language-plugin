package com.niikelion.ic10_language.ui.swing.layout

import com.intellij.util.ui.WrapLayout
import com.niikelion.ic10_language.ui.swing.Content
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JPanel

fun SwingBuilder.wrap(options: BlockOptions, verticalGap: Int = 0, content: Content) = element(content) { elements ->
    object : JPanel(WrapLayout(options.justification.flow, options.gap, verticalGap)) {
        override fun getPreferredSize(): Dimension {
            // WrapLayout uses this component's width to compute row-wrapping.
            // Before the first layout pass our width is 0, so WrapLayout walks
            // up the parent chain — but it doesn't subtract intermediate insets,
            // causing it to think one extra item fits per row and report a height
            // that's one row too short.  Seed our own size from the parent chain
            // (correctly accounting for insets) so the very first measurement is right.
            if (size.width == 0) {
                val avail = availableParentWidth()
                if (avail > 0) setSize(avail, 0)
            }
            return super.getPreferredSize()
        }

        private fun availableParentWidth(): Int {
            var accumulated = 0
            var c = parent
            while (c != null) {
                if (c.size.width > 0) {
                    val ins = c.insets
                    return c.size.width - ins.left - ins.right - accumulated
                }
                val ins = c.insets
                accumulated += ins.left + ins.right
                c = c.parent
            }
            return 0
        }
    }.apply {
        elements.forEach(::add)
    }
}
fun SwingBuilder.wrap(optionsBuilder: BlockOptionsBuilder.() -> Unit, verticalGap: Int = 0, content: Content) =
    wrap(BlockOptionsBuilder.build(optionsBuilder), verticalGap, content)

fun SwingBuilder.wrap(verticalGap: Int = 0, content: Content) =
    wrap(BlockOptions(), verticalGap, content)

val Justification.flow get() = when(this) {
    Justification.START -> FlowLayout.LEFT
    Justification.CENTER -> FlowLayout.CENTER
    Justification.END -> FlowLayout.RIGHT
    Justification.FILL -> FlowLayout.CENTER
}