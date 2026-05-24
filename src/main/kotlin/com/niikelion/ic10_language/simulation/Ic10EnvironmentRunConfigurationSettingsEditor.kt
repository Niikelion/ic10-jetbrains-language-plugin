package com.niikelion.ic10_language.simulation

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class Ic10EnvironmentRunConfigurationSettingsEditor : SettingsEditor<Ic10EnvironmentRunConfiguration>() {
    private val configFileField = TextFieldWithBrowseButton()

    private val panel: JComponent by lazy {
        configFileField.addBrowseFolderListener(
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor("ic10env")
                .withTitle("Select IC10 Environment Config")
                .withDescription("Choose an .ic10env file")
        )
        FormBuilder.createFormBuilder()
            .addLabeledComponent("Environment file:", configFileField)
            .panel
    }

    override fun resetEditorFrom(config: Ic10EnvironmentRunConfiguration) {
        configFileField.text = config.configFile
    }

    override fun applyEditorTo(config: Ic10EnvironmentRunConfiguration) {
        config.configFile = configFileField.text.trim()
    }

    override fun createEditor(): JComponent = panel
}
