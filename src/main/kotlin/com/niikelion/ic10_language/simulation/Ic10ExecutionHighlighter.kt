package com.niikelion.ic10_language.simulation

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.util.application
import com.intellij.xdebugger.ui.DebuggerColors
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.ui.swing.state.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.swing.Icon

class Ic10ExecutionHighlighter(
    private val project: Project,
    private val process: SimulationProcessProxy,
    selectedDevice: StateFlow<Long?>,
    scope: Disposable
) {
    private val activeHighlighters = mutableListOf<Pair<MarkupModel, RangeHighlighter>>()

    init {
        combine(selectedDevice, process.stateFlow) { deviceId, state ->
            deviceId to state
        }.onEach { (deviceId, state) ->
            val position = resolvePosition(deviceId, state)
            application.invokeLater {
                if (!project.isDisposed) applyHighlight(position)
            }
        }.launchIn(scope.coroutineScope())

        Disposer.register(scope, Disposable {
            application.invokeLater {
                if (!project.isDisposed) clearHighlight()
            }
        })
    }

    private class ExecutionPosition(val lineNumber: Int, val editors: Array<Editor>)

    private fun resolvePosition(deviceId: Long?, state: SimulationState): ExecutionPosition? {
        deviceId ?: return null
        val device = process.context.devices.find { it.id == deviceId } ?: return null
        val programAspect = device.aspect<Ic10ProgramAspect>() ?: return null
        val programState = state.devices[deviceId]
            ?.aspects?.get(Ic10ProgramAspect.State::class) as? Ic10ProgramAspect.State
            ?: return null
        val sourceLine = programAspect.code.lines.getOrNull(programState.instructionIndex)
            ?.sourceLine?.takeIf { it >= 0 } ?: return null
        val virtualFile = programAspect.code.source.virtualFile ?: return null
        val document = FileDocumentManager.getInstance().getDocument(virtualFile) ?: return null
        val editors = EditorFactory.getInstance().getEditors(document, project)
        if (editors.isEmpty()) return null
        return ExecutionPosition(sourceLine, editors)
    }

    private fun applyHighlight(position: ExecutionPosition?) {
        clearHighlight()
        position ?: return
        for (editor in position.editors) {
            val highlighter = editor.markupModel.addLineHighlighter(
                DebuggerColors.EXECUTIONPOINT_ATTRIBUTES,
                position.lineNumber,
                HighlighterLayer.SELECTION - 1
            )
            highlighter.gutterIconRenderer = ExecutionArrowRenderer
            activeHighlighters.add(editor.markupModel to highlighter)
        }
    }

    private fun clearHighlight() {
        activeHighlighters.forEach { (markupModel, highlighter) ->
            markupModel.removeHighlighter(highlighter)
        }
        activeHighlighters.clear()
    }

    private object ExecutionArrowRenderer : GutterIconRenderer() {
        override fun getIcon(): Icon = AllIcons.General.ArrowRight
        override fun getAlignment() = Alignment.LEFT
        override fun getTooltipText() = "Current execution point"
        override fun equals(other: Any?) = other === this
        override fun hashCode() = System.identityHashCode(this)
    }
}
