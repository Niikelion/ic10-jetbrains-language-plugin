package com.niikelion.ic10_language.simulation

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.util.application
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.Ic10Icons
import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.logic.CompilationError
import com.niikelion.ic10_language.logic.Context
import com.niikelion.ic10_language.logic.ProgramCode
import com.niikelion.ic10_language.logic.devices.StructureCircuitHousing
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.psi.Ic10File
import com.niikelion.ic10_language.utils.mapSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.OutputStream

private class Terminated: ProcessHandler() {
    init {
        startNotify()
        notifyProcessTerminated(1)
    }

    override fun destroyProcessImpl() = notifyProcessTerminated(1)
    override fun detachProcessImpl() = notifyProcessTerminated(1)
    override fun detachIsDefault(): Boolean = false
    override fun getProcessInput(): OutputStream? = null

}

@Service(Service.Level.PROJECT)
class SimulationService(private val project: Project) {
    private val process = MutableStateFlow<SimulationProcess?>(null)
    val processFlow = process.mapSync { if (it == null) null else SimulationProcessProxy(it.context, it.state.asStateFlow()) }

    private fun startNewProcess(context: Context): SimulationProcess {
        process.value?.run { stop() }
        return SimulationProcess(context).also {
            process.value = it
            SimulatorWindow.get(project)?.also { window ->
                window.isAvailable = true
                window.show()
            }
            it.addProcessListener(object: ProcessListener {
                override fun processTerminated(event: ProcessEvent) {
                    process.value = null
                    application.invokeLater {
                        SimulatorWindow.get(project)?.isAvailable = false
                    }
                }
            })
        }
    }

    fun start(console: ConsoleView, filePaths: List<String>): ProcessHandler {
        if (filePaths.isEmpty()) {
            console.print("ERROR: No source files configured.\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        val devices = mutableListOf<StructureCircuitHousing>()
        var nextId = 1L

        for (filePath in filePaths) {
            try {
                val file = LocalFileSystem.getInstance().findFileByPath(filePath)?.findPsiFile(project)
                    ?: ReadAction.compute<PsiFile, Throwable> {
                        val text = File(filePath).readText()
                        PsiFileFactory.getInstance(project).createFileFromText("Program.ic10", Ic10FileType.Instance, text)
                    }

                if (file !is Ic10File) {
                    console.print("ERROR: $filePath is not an IC10 file\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
                    continue
                }

                console.print("Compiling $filePath\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
                val code = ProgramCode.compile(file)
                devices.add(StructureCircuitHousing(nextId++, code))
            } catch (t: Throwable) {
                if (t is CompilationError) {
                    val line = t.target?.let { Ic10PsiUtils.getLineNumber(it) }?.let { "($it)" } ?: ""
                    console.print("Compilation error$line in $filePath: ${t.message}\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
                } else {
                    console.print("ERROR: Could not load $filePath: ${t.message}\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
                }
            }
        }

        if (devices.isEmpty()) {
            console.print("ERROR: No devices could be compiled.\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        console.print("Starting simulation with ${devices.size} device(s)\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        return startNewProcess(Context(devices))
    }
}

class SimulationProcessProxy(val context: Context, val stateFlow: StateFlow<SimulationState>) {
    override fun equals(other: Any?): Boolean = other is SimulationProcessProxy && other.context == context
    override fun hashCode(): Int {
        return context.hashCode()
    }
}

class SimulationProcess(val context: Context): ProcessHandler() {
    val state = MutableStateFlow(context.initialize())
    private val history = mutableListOf<SimulationState.StateChange>()
    val canGoBack get() = history.isNotEmpty()

    init {
        startNotify()
    }

    fun step() {
        val change = context.tick(state.value)
        history.add(change)
        state.value = change.perform(state.value)
        notifyTextAvailable("History at ${history.size}\n", ProcessOutputTypes.STDOUT)
    }

    fun stepBack() {
        val latestChange = history.removeLastOrNull() ?: return
        state.value = latestChange.revert(state.value)
        notifyTextAvailable("History at ${history.size}\n", ProcessOutputTypes.STDOUT)
    }

    fun stop() {
        state.value = SimulationState()
        history.clear()
        notifyProcessTerminated(0)
    }

    override fun destroyProcessImpl() = stop()
    override fun detachProcessImpl() = stop()

    override fun detachIsDefault(): Boolean = false
    override fun getProcessInput(): OutputStream? = null
}

class StepSimulationAction(
    private val process: SimulationProcess
): AnAction("Step", "Computes next step of the simulation", Ic10Icons.Step) {
    override fun actionPerformed(event: AnActionEvent) = process.step()
}

class StepBackSimulationAction(
    private val process: SimulationProcess
): AnAction("Step Back", "Goes back to the previous step of the simulation", Ic10Icons.StepBack) {
    override fun actionPerformed(event: AnActionEvent) = process.stepBack()
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = process.canGoBack
    }

    override fun getActionUpdateThread() = ActionUpdateThread.EDT
}