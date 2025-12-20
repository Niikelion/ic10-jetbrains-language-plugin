package com.niikelion.ic10_language.simulation

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class Ic10SimulatorWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val panel = Ic10SimulatorWindowPanel()
        val content = ContentFactory.getInstance().createContent(panel, null, false)

        toolWindow.contentManager.addContent(content)
    }
}