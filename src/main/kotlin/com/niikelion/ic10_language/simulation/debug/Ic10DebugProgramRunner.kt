package com.niikelion.ic10_language.simulation.debug

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.components.service
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.niikelion.ic10_language.simulation.Ic10RunConfiguration
import com.niikelion.ic10_language.simulation.SimulationProcess
import com.niikelion.ic10_language.simulation.SimulationService

class Ic10DebugProgramRunner : GenericProgramRunner<RunnerSettings>() {
    override fun getRunnerId() = "Ic10DebugProgramRunner"

    override fun canRun(executorId: String, profile: RunProfile) =
        executorId == DefaultDebugExecutor.EXECUTOR_ID && profile is Ic10RunConfiguration

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        val config = environment.runProfile as? Ic10RunConfiguration ?: return null
        val project = environment.project
        val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        val process = project.service<SimulationService>().start(console, config.sources)
        if (process !is SimulationProcess) return null

        return XDebuggerManager.getInstance(project).startSession(environment, object : XDebugProcessStarter() {
            override fun start(session: XDebugSession): XDebugProcess =
                Ic10DebugProcess(session, process, console)
        }).runContentDescriptor
    }
}
