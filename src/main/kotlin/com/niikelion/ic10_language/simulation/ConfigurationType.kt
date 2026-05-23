package com.niikelion.ic10_language.simulation

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.util.NotNullLazyValue
import com.niikelion.ic10_language.Ic10Icons

class Ic10RunConfigurationType: ConfigurationTypeBase(ID, "Ic10 Debug", "Ic10 debug configuration", NotNullLazyValue.createValue { Ic10Icons.File }) {
    companion object {
        const val ID = "RunIc10Configuration"
    }

    init {
        addFactory(Ic10RunConfigurationFactory(this))
    }
}