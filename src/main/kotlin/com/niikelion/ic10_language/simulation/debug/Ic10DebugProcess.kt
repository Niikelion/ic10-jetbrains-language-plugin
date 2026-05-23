package com.niikelion.ic10_language.simulation.debug

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.icons.AllIcons
import com.intellij.util.application
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XSuspendContext
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.Ic10Icons
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.simulation.SimulationProcess
import java.util.concurrent.ConcurrentHashMap

class Ic10DebugProcess(
    session: XDebugSession,
    private val process: SimulationProcess,
    private val console: ConsoleView
) : XDebugProcess(session) {

    private val breakpoints = ConcurrentHashMap<String, MutableSet<Int>>()
    @Volatile private var pauseRequested = false
    @Volatile var isRunning = false
        private set

    private val suspendContext = Ic10SuspendContext(process, process.context.devices)

    override fun doGetProcessHandler(): ProcessHandler = process

    override fun createConsole() = console

    override fun getEditorsProvider(): XDebuggerEditorsProvider = object : XDebuggerEditorsProvider() {
        override fun getFileType(): FileType = Ic10FileType.Instance
        override fun createDocument(
            project: Project,
            expression: com.intellij.xdebugger.XExpression,
            sourcePosition: XSourcePosition?,
            mode: EvaluationMode
        ): Document = EditorFactory.getInstance().createDocument(expression.expression)
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> = arrayOf(
        Ic10BreakpointHandler(
            addBreakpoint = { url, line ->
                breakpoints.getOrPut(url) { ConcurrentHashMap.newKeySet() }.add(line)
            },
            removeBreakpoint = { url, line -> breakpoints[url]?.remove(line) }
        )
    )

    override fun sessionInitialized() {
        application.executeOnPooledThread {
            session.positionReached(suspendContext)
        }
    }

    override fun startPausing() {
        pauseRequested = true
    }

    override fun resume(context: XSuspendContext?) {
        pauseRequested = false
        isRunning = true
        application.executeOnPooledThread {
            var lastUpdate = System.currentTimeMillis()
            do {
                if (process.isProcessTerminated) {
                    isRunning = false
                    return@executeOnPooledThread
                }
                process.advanceStepSilent()
                val now = System.currentTimeMillis()
                if (now - lastUpdate >= 1000) {
                    process.publishCurrentState()
                    lastUpdate = now
                }
            } while (!isAtBreakpoint() && !pauseRequested)
            isRunning = false
            process.publishCurrentState()
            if (!process.isProcessTerminated) session.positionReached(suspendContext)
        }
    }

    override fun startStepOver(context: XSuspendContext?) {
        application.executeOnPooledThread {
            process.advanceStep()
            if (!process.isProcessTerminated) session.positionReached(suspendContext)
        }
    }

    override fun startStepInto(context: XSuspendContext?) = startStepOver(context)

    override fun startStepOut(context: XSuspendContext?) = startStepOver(context)

    override fun stop() = process.stop()

    override fun registerAdditionalActions(
        leftToolbar: DefaultActionGroup,
        topToolbar: DefaultActionGroup,
        settings: DefaultActionGroup
    ) {
        topToolbar.add(DebugPauseAction())
        topToolbar.add(DebugStepBackAction())
        topToolbar.add(DebugTickAction())
    }

    private fun isAtBreakpoint(): Boolean {
        val state = process.currentState
        return process.context.devices.any { device ->
            val programAspect = device.aspect<Ic10ProgramAspect>() ?: return@any false
            val deviceState = state.devices[device.id] ?: return@any false
            val programState = deviceState.aspect<Ic10ProgramAspect.State>() ?: return@any false
            val sourceLine = programAspect.code.lines.getOrNull(programState.instructionIndex)
                ?.sourceLine?.takeIf { it >= 0 } ?: return@any false
            val fileUrl = programAspect.code.source.virtualFile?.url ?: return@any false
            breakpoints[fileUrl]?.contains(sourceLine) == true
        }
    }

    private inner class DebugPauseAction : AnAction(
        "Pause", "Pauses the simulation", AllIcons.Actions.Pause
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            session.pause()
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = isRunning
        }

        override fun getActionUpdateThread() = ActionUpdateThread.BGT
    }

    private inner class DebugTickAction : AnAction(
        "Step Forward", "Executes one full simulation tick", Ic10Icons.Step
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            application.executeOnPooledThread {
                process.tick()
                if (!process.isProcessTerminated) session.positionReached(suspendContext)
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = !isRunning
        }

        override fun getActionUpdateThread() = ActionUpdateThread.BGT
    }

    private inner class DebugStepBackAction : AnAction(
        "Step Back", "Reverts to beginning of current tick, or to previous tick", Ic10Icons.StepBack
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            application.executeOnPooledThread {
                process.stepBack()
                if (!process.isProcessTerminated) session.positionReached(suspendContext)
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = process.canGoBack && !isRunning
        }

        override fun getActionUpdateThread() = ActionUpdateThread.BGT
    }
}
