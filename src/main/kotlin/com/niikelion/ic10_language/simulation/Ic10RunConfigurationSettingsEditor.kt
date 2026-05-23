package com.niikelion.ic10_language.simulation

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.niikelion.ic10_language.Ic10FileType
import javax.swing.DefaultListModel
import javax.swing.JComponent

class Ic10RunConfigurationSettingsEditor: SettingsEditor<Ic10RunConfiguration>() {
    private val listModel = DefaultListModel<String>()
    private val fileList = JBList(listModel)

    private val panel: JComponent by lazy {
        ToolbarDecorator.createDecorator(fileList)
            .setAddAction { _ ->
                val descriptor = FileChooserDescriptor(true, false, false, false, false, true)
                    .withFileFilter { it.fileType == Ic10FileType.Instance }
                    .withTitle("Select IC10 Source File(s)")
                FileChooserFactory.getInstance()
                    .createFileChooser(descriptor, null, null)
                    .choose(null)
                    .forEach { vf -> if (!listModel.contains(vf.path)) listModel.addElement(vf.path) }
            }
            .setRemoveAction { _ ->
                fileList.selectedIndices.sortedDescending().forEach { listModel.remove(it) }
            }
            .createPanel()
    }

    override fun resetEditorFrom(runConfiguration: Ic10RunConfiguration) {
        listModel.clear()
        runConfiguration.sources.forEach { listModel.addElement(it) }
    }

    override fun applyEditorTo(runConfiguration: Ic10RunConfiguration) {
        runConfiguration.sources = (0 until listModel.size).map { listModel.getElementAt(it) }
    }

    override fun createEditor(): JComponent = panel
}
