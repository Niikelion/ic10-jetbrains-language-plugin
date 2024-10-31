package com.niikelion.ic10_language.tools

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel

class Ic10RunnerWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(createFileRunner(), "File", false)

        toolWindow.contentManager.addContent(content)
    }

    private fun createFileRunner(): JPanel = FileRunner().contentPane!!
}