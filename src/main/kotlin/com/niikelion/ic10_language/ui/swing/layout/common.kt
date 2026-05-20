package com.niikelion.ic10_language.ui.swing.layout

import com.intellij.ui.components.JBBox
import com.intellij.util.ui.components.BorderLayoutPanel
import com.niikelion.ic10_language.ui.swing.Content
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.BoxLayout
import javax.swing.JComponent

enum class Alignment {
    START {
        override val value = 0f
    },
    CENTER {
        override val value = 0.5f
    },
    END {
        override val value = 1f
    };

    abstract val value: Float
}

enum class Justification {
    START {
        override val vertical get() = BorderLayout.NORTH
        override val horizontal get() = BorderLayout.WEST
    },
    CENTER {
        override val vertical get() = BorderLayout.CENTER
        override val horizontal get() = BorderLayout.CENTER
    },
    END {
        override val vertical get() = BorderLayout.SOUTH
        override val horizontal get() = BorderLayout.EAST
    },
    FILL {
        override val vertical get() = BorderLayout.CENTER
        override val horizontal get() = BorderLayout.CENTER
    };

    abstract val vertical: String
    abstract val horizontal: String
}

data class BlockOptions(
    val gap: Int = 0,
    val alignment: Alignment = Alignment.START,
    val justification: Justification = Justification.START
) {

    fun gap(gap: Int): BlockOptions = copy(gap = gap)
    fun align(alignment: Alignment): BlockOptions = copy(alignment = alignment)
    fun justify(justification: Justification): BlockOptions = copy(justification = justification)
}

class BlockOptionsBuilder(private var options: BlockOptions = BlockOptions()) {
    companion object {
        fun build(builder: BlockOptionsBuilder.() -> Unit) = BlockOptionsBuilder().apply(builder).value
    }

    val value get() = options

    fun gap(gap: Int) {
        this.options = this.options.gap(gap)
    }
    fun align(alignment: Alignment) {
        this.options = this.options.align(alignment)
    }
    fun justify(justification: Justification) {
        this.options = this.options.justify(justification)
    }
}

enum class BoxAxis {
    VERTICAL {
        override val boxLayoutAxis get() = BoxLayout.Y_AXIS
        override fun createFiller(): Component = JBBox.createVerticalGlue()
    },
    HORIZONTAL {
        override val boxLayoutAxis get() = BoxLayout.X_AXIS
        override fun createFiller(): Component = JBBox.createHorizontalGlue()
    };

    abstract val boxLayoutAxis: Int
    abstract fun createFiller(): Component
}

fun SwingBuilder.box(axis: BoxAxis, options: BlockOptions, content: Content) = element(content) { elements ->
    object: JBBox(axis.boxLayoutAxis) {
        override fun add(comp: Component?): Component? {
            val jComp = comp as? JComponent
            when (axis) {
                BoxAxis.VERTICAL -> jComp?.alignmentX = options.alignment.value
                BoxAxis.HORIZONTAL -> jComp?.alignmentY = options.alignment.value
            }
            return super.add(comp)
        }
    }.also {
        when (options.justification) {
            Justification.END, Justification.CENTER ->
                it.add(axis.createFiller())

            else -> {}
        }

        elements.forEachIndexed { index, element ->
            if (index > 0 && options.justification == Justification.FILL) it.add(axis.createFiller())
            it.add(element)
        }

        when (options.justification) {
            Justification.CENTER, Justification.START ->
                it.add(axis.createFiller())
            else -> {}
        }
        it.name = "box"
    }.let { box -> BorderLayoutPanel().also {
            it.add(box, when (axis) {
                BoxAxis.VERTICAL -> options.justification.vertical
                BoxAxis.HORIZONTAL -> options.justification.horizontal
            })
        }
    }
}

fun SwingBuilder.box(axis: BoxAxis, optionsBuilder: BlockOptionsBuilder.() -> Unit, content: Content) =
    box(axis, BlockOptionsBuilder.build(optionsBuilder), content)

fun SwingBuilder.row(options: BlockOptions, content: Content) =
    box(BoxAxis.HORIZONTAL, options, content).also { it.name = "row" }
fun SwingBuilder.row(optionsBuilder: BlockOptionsBuilder.() -> Unit, content: Content) =
    row(BlockOptionsBuilder.build(optionsBuilder), content)
fun SwingBuilder.row(content: Content) =
    row(BlockOptions(), content)

fun SwingBuilder.column(options: BlockOptions, content: Content) =
    box(BoxAxis.VERTICAL, options, content).also { it.name = "column" }
fun SwingBuilder.column(optionsBuilder: BlockOptionsBuilder.() -> Unit, content: Content) =
    column(BlockOptionsBuilder.build(optionsBuilder), content)
fun SwingBuilder.column(content: Content) =
    column(BlockOptions(), content)