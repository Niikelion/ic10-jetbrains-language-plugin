package com.niikelion.ic10_language.simulation

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.service
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class Ic10RunConfiguration(
    project: Project,
    factory: ConfigurationFactory?,
    name: String
) : RunConfigurationBase<Ic10RunConfigurationOptions>(project, factory, name) {
    protected override fun getOptions() = super.getOptions() as Ic10RunConfigurationOptions

    var sources: List<String>
        get() = options.getSources()
        set(value) = options.setSources(value)

    override fun getState(
        executor: Executor,
        environment: ExecutionEnvironment
    ): RunProfileState {
        return RunProfileState { _, _ ->
            val console = TextConsoleBuilderFactory.getInstance().createBuilder(environment.project).console
            val processHandler = project.service<SimulationService>().start(console, sources)

            val actions = if (processHandler is SimulationProcess) arrayOf(
                StepBackSimulationAction(processHandler),
                StepSimulationAction(processHandler),
                TickSimulationAction(processHandler)
            ) else emptyArray()

            console.attachToProcess(processHandler)
            DefaultExecutionResult(console, processHandler, *actions)
        }
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> = Ic10RunConfigurationSettingsEditor()
}
