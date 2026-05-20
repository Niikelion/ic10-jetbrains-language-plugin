package com.niikelion.ic10_language.simulation

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SearchTextField
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.treeStructure.Tree
import com.niikelion.ic10_language.Ic10Icons
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import com.niikelion.ic10_language.ui.swing.jb.label
import com.niikelion.ic10_language.ui.swing.jb.split
import com.niikelion.ic10_language.ui.swing.layout.*
import com.niikelion.ic10_language.ui.swing.state.reactive
import com.niikelion.ic10_language.utils.mapSync
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class SimulatorWindow(
    private val project: Project,
    private val toolWindow: ToolWindow
) {
    companion object {
        fun get(project: Project) = ToolWindowManager.getInstance(project).getToolWindow("Ic10 Simulator")
    }

    fun createContent(): Content {
        val panel = SwingBuilder.panel {
            val processFlow = project.service<SimulationService>().processFlow
            reactive(toolWindow.disposable, processFlow) { state, scope ->
                if (state == null) return@reactive centered { label("No simulation is running.") }
                return@reactive StatePanel(state, scope, project).render(this)
            }
        }
        return ContentFactory.getInstance().createContent(panel, null, false)
    }

    fun setupContent() {
        toolWindow.contentManager.addContent(createContent())
    }

    class StatePanel(private val process: SimulationProcessProxy, private val scope: Disposable, private val project: Project) {
        private val selectedDevice = MutableStateFlow<Long?>(null)

        init {
            Ic10ExecutionHighlighter(project, process, selectedDevice, scope)
        }

        val render
            get(): SwingBuilder.() -> JComponent = {
                split(vertically = true) {
                    deviceSelector()
                    deviceInspector()
                }
            }

        val deviceSelector
            get(): SwingBuilder.() -> Unit = {
                val devices = process.context.devices

                val treeRoot = DefaultMutableTreeNode("Devices")
                devices.forEach { device ->
                    treeRoot.add(DefaultMutableTreeNode(device))
                }

                val treeModel = DefaultTreeModel(treeRoot)

                column({
                    align(Alignment.CENTER)
                    justify(Justification.START)
                }) {
                    element {
                        SearchTextField().also {
                            it.maximumSize = Dimension(Int.MAX_VALUE, it.preferredSize.height)
                        }
                    }
                    column({
                        justify(Justification.START)
                        align(Alignment.START)
                    }) {
                        scrollable(Scroll.AUTO, Scroll.AUTO) {
                            element { Tree(treeModel) }.also {
                                it.cellRenderer = object : ColoredTreeCellRenderer() {
                                    override fun customizeCellRenderer(
                                        tree: JTree,
                                        value: Any?,
                                        selected: Boolean,
                                        expanded: Boolean,
                                        leaf: Boolean,
                                        row: Int,
                                        hasFocus: Boolean
                                    ) {
                                        val node = value as? DefaultMutableTreeNode ?: return
                                        when (val userValue = node.userObject) {
                                            is String -> append(userValue)
                                            is Device -> {
                                                append(userValue.name)

                                                val program = userValue.aspect<Ic10ProgramAspect>()
                                                program?.run { append("(${code.source.name})") }

                                                append(" ${userValue.id}", SimpleTextAttributes.GRAY_ATTRIBUTES)

                                                program?.run { icon = Ic10Icons.File }
                                            }
                                        }
                                    }
                                }
                                it.isRootVisible = false
                                it.addTreeSelectionListener { event ->
                                    val node = event.path?.lastPathComponent as? DefaultMutableTreeNode
                                    val source = node?.userObject as? Device
                                    selectedDevice.value = source?.id
                                }
                            }
                        }
                    }
                }
            }
        val deviceInspector
            get(): SwingBuilder.() -> Unit = {
                reactive(scope, selectedDevice) { deviceId, scope ->
                    val device = process.context.devices.find { it.id == deviceId } ?: return@reactive noDeviceSelected()
                    scrollable {
                        element {
                            device.renderDebuggerView(
                                process.stateFlow.mapSync { simulation -> simulation.devices[device.id]!! },
                                scope
                            )
                        }
                    }
                }
            }
    }
}

private fun SwingBuilder.noDeviceSelected() = centered { label("No device selected.") }

private fun SwingBuilder.centered(content: com.niikelion.ic10_language.ui.swing.Content) = row({
    justify(Justification.CENTER)
    align(Alignment.CENTER)
}, content).also { it.preferredSize = it.maximumSize }