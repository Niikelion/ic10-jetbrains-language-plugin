package com.niikelion.ic10_language.tools

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import com.niikelion.ic10_language.Ic10Icons
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class RunnerToggleAction(name: String, toolTip: String, icon: Icon, private val accessor: () -> Boolean, private val mutator: (value: Boolean) -> Unit): ToggleAction(name, toolTip, icon), DumbAware,
    CustomComponentAction {
    override fun isSelected(e: AnActionEvent): Boolean = accessor()

    override fun setSelected(e: AnActionEvent, state: Boolean) = mutator(state)

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        val button = JButton(presentation.text)

        updateButtonBackground(button, accessor())

        button.addActionListener {
            val state = !accessor()

            mutator(state)
            Toggleable.setSelected(presentation, state)
            updateButtonBackground(button, state)
        }

        button.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseEntered(e: java.awt.event.MouseEvent?) {
                button.background = JBColor.CYAN
            }
            override fun mouseExited(e: java.awt.event.MouseEvent?) {
                button.background = JBColor.BLUE
            }
        })

        return button
    }

    private fun updateButtonBackground(button: JButton, selected: Boolean) {
        button.background = if (selected) JBColor.GREEN else JBColor.BLUE
        button.foreground = JBColor.WHITE
    }
}

class Ic10RunnerWindowFactory: ToolWindowFactory {
    private var running = false

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(createFileRunner(), "File", false)

        toolWindow.contentManager.addContent(content)
    }

    private fun createFileRunner(): JPanel {
        val content = JPanel(BorderLayout())

        val actionManager = ActionManager.getInstance()
        val actionGroup = DefaultActionGroup("Ic10FileRunnerActionGroup", false)

        actionGroup.addAll(
            RunnerToggleAction(
                "Run",
                "Run Single File",
                Ic10Icons.Debug,
                this::running,
                this::running::set
            )
        )

        val actionToolbar = actionManager.createActionToolbar("Ic10FileRunnerActionToolbar", actionGroup, true)
        actionToolbar.targetComponent = null

        actionToolbar.setMiniMode(false)

        val toolbarPanel = JPanel(HorizontalLayout(0))
        toolbarPanel.add(actionToolbar.component)

        content.add(toolbarPanel, BorderLayout.NORTH)

        return content
    }
}