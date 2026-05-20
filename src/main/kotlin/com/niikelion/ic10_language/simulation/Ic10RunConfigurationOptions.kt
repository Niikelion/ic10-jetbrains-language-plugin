package com.niikelion.ic10_language.simulation

import com.intellij.execution.configurations.RunConfigurationOptions

class Ic10RunConfigurationOptions: RunConfigurationOptions() {
    private val sourcesProperty = string("").provideDelegate(this, "sources")
    private val legacySourceProperty = string("").provideDelegate(this, "source")   // read-only compat

    fun getSources(): List<String> {
        val raw = sourcesProperty.getValue(this) ?: ""
        if (raw.isNotBlank()) return raw.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        val legacy = legacySourceProperty.getValue(this) ?: ""
        return if (legacy.isNotBlank()) listOf(legacy) else emptyList()
    }

    fun setSources(values: List<String>) = sourcesProperty.setValue(this, values.joinToString("\n"))
}
