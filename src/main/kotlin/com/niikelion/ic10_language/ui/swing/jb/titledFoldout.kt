package com.niikelion.ic10_language.ui.swing.jb

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBUI
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities

class HideableTitledPanel(
    title: String,
    content: JComponent,
    expandedInitially: Boolean = true
) : JPanel(BorderLayout()) {

    private val arrowLabel = JBLabel()
    private val contentPanel = JBPanel<JBPanel<*>>(BorderLayout())
    private val header = JBPanel<JBPanel<*>>(BorderLayout())
    private var expanded = expandedInitially

    init {
        isOpaque = false

        // Header
        header.apply {
            isOpaque = false
            border = JBUI.Borders.empty(6, 0)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }

        arrowLabel.icon = if (expanded) AllIcons.General.ArrowDown else AllIcons.General.ArrowRight

        val titleLabel = JBLabel(title).apply {
            border = JBUI.Borders.emptyLeft(6)
        }

        header.add(arrowLabel, BorderLayout.WEST)
        header.add(titleLabel, BorderLayout.CENTER)

        // Content
        contentPanel.isOpaque = false
        contentPanel.border = JBUI.Borders.empty(0, 18, 8, 0)
        contentPanel.add(content, BorderLayout.CENTER)
        contentPanel.isVisible = expanded

        header.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                toggle()
            }
        })

        add(header, BorderLayout.NORTH)
        add(contentPanel, BorderLayout.CENTER)
    }

    private fun toggle() {
        expanded = !expanded
        arrowLabel.icon = if (expanded) AllIcons.General.ArrowDown else AllIcons.General.ArrowRight
        contentPanel.isVisible = expanded
        contentPanel.revalidate()
        contentPanel.repaint()
    }

    override fun getPreferredSize(): Dimension {
        val base = super.getPreferredSize()
        return if (expanded) base else Dimension(base.width, header.preferredSize.height)
    }
}

fun SwingBuilder.titledFoldout(title: String, content: SwingBuilder.() -> JComponent) = element {
    HideableTitledPanel(title, SwingBuilder().content()).apply {
        SwingUtilities.invokeLater {
            revalidate()
            repaint()
            parent?.revalidate()
            parent?.repaint()
        }
    }
}