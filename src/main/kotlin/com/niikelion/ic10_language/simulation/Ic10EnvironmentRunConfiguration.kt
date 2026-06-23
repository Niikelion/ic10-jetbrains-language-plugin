package com.niikelion.ic10_language.simulation

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class Ic10EnvironmentRunConfiguration(
    project: Project,
    factory: ConfigurationFactory?,
    name: String
) : RunConfigurationBase<Ic10EnvironmentRunConfigurationOptions>(project, factory, name) {

    override fun getOptions() = super.getOptions() as Ic10EnvironmentRunConfigurationOptions

    var configFile: String
        get() = options.configFile
        set(value) { options.configFile = value }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
        RunProfileState { _, _ -> null }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        Ic10EnvironmentRunConfigurationSettingsEditor()
}
