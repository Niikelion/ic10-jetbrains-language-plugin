package com.niikelion.ic10_language.simulation

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NonNls

class Ic10RunConfigurationFactory(
    val type: Ic10RunConfigurationType
): ConfigurationFactory(type) {
    override fun getId(): @NonNls String = Ic10RunConfigurationType.ID

    override fun createTemplateConfiguration(project: Project): RunConfiguration = Ic10RunConfiguration(project, this, "Ic10")
    override fun getOptionsClass(): Class<out BaseState?> = Ic10RunConfigurationOptions::class.java
}