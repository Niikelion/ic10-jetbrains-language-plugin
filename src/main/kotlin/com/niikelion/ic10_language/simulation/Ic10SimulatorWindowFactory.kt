package com.niikelion.ic10_language.simulation

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class Ic10SimulatorWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val window = SimulatorWindow(project, toolWindow)
        window.setupContent()
    }

    override fun shouldBeAvailable(project: Project): Boolean = false
}