package com.niikelion.ic10_language.simulation

import com.intellij.execution.configurations.RunConfigurationOptions

class Ic10EnvironmentRunConfigurationOptions : RunConfigurationOptions() {
    private val configFileProperty = string("").provideDelegate(this, "configFile")

    var configFile: String
        get() = configFileProperty.getValue(this) ?: ""
        set(value) = configFileProperty.setValue(this, value)
}
