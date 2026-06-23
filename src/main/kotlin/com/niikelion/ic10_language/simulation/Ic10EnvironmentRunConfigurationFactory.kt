package com.niikelion.ic10_language.simulation

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NonNls

class Ic10EnvironmentRunConfigurationFactory(
    type: Ic10EnvironmentConfigurationType
) : ConfigurationFactory(type) {
    override fun getId(): @NonNls String = Ic10EnvironmentConfigurationType.ID

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        Ic10EnvironmentRunConfiguration(project, this, "Ic10 Environment")

    override fun getOptionsClass(): Class<out BaseState> = Ic10EnvironmentRunConfigurationOptions::class.java
}
