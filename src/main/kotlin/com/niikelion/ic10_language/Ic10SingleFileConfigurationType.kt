package com.niikelion.ic10_language

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.StoredProperty
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.NotNullLazyValue
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


val ID = "Ic10DebugSingleFileConfiguration"

class Ic10SingleFileConfigurationType: ConfigurationTypeBase(
    ID,
    "Debug Ic10",
    "Debug single Ic10 file",
    NotNullLazyValue.createValue { AllIcons.Actions.StartDebugger }
) {
    init { addFactory(Ic10SingleFileConfigurationFactory(this)) }
}

class Ic10SingleFileConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String = ID
    override fun createTemplateConfiguration(project: Project): RunConfiguration = Ic10DebugSingleFileConfiguration(project, this, "Debug Ic10")
    override fun getOptionsClass(): Class<out BaseState> = Ic10DebugSingleFileConfigurationOptions::class.java
}

class Ic10DebugSingleFileConfigurationOptions: RunConfigurationOptions() {
    private val scriptNameProperty: StoredProperty<String?> = string("").provideDelegate(this, "scriptName")

    var scriptName: String?
        set(value) = scriptNameProperty.setValue(this, value);
        get() = scriptNameProperty.getValue(this)
}

class Ic10DebugSingleFileConfiguration(project: Project, factory: ConfigurationFactory, name: String): RunConfigurationBase<Ic10DebugSingleFileConfigurationOptions>(project, factory, name) {
    override fun getOptions(): Ic10DebugSingleFileConfigurationOptions = super.getOptions() as Ic10DebugSingleFileConfigurationOptions
    var scriptName: String?
        get() = options.scriptName;
        set(value) { options.scriptName = value }
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = Ic10DebugSettingsEditor()
    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return object: CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                val commandLine = GeneralCommandLine("echo ${options.scriptName}")
                val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }
}

class Ic10DebugSettingsEditor: SettingsEditor<Ic10DebugSingleFileConfiguration>() {
    private val panel: JPanel
    private val scriptPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init {
        scriptPathField.addBrowseFolderListener("Select Script File", null, null, FileChooserDescriptorFactory.createSingleFileDescriptor(Ic10FileType.Instance))
        panel = FormBuilder.createFormBuilder().addLabeledComponent("Script file", scriptPathField).panel
    }

    override fun resetEditorFrom(configuration: Ic10DebugSingleFileConfiguration) {
        scriptPathField.text = configuration.scriptName!!
    }
    override fun applyEditorTo(configuration: Ic10DebugSingleFileConfiguration) {
        configuration.scriptName = scriptPathField.text
    }
    override fun createEditor(): JComponent = panel
}