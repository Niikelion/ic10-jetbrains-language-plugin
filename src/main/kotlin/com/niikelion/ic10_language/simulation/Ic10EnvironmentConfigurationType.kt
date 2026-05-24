package com.niikelion.ic10_language.simulation

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.util.NotNullLazyValue
import com.niikelion.ic10_language.Ic10Icons

class Ic10EnvironmentConfigurationType : ConfigurationTypeBase(
    ID,
    "Ic10 Environment",
    "Ic10 multi-device environment simulation",
    NotNullLazyValue.createValue { Ic10Icons.File }
) {
    companion object {
        const val ID = "RunIc10EnvironmentConfiguration"
    }

    init {
        addFactory(Ic10EnvironmentRunConfigurationFactory(this))
    }
}
