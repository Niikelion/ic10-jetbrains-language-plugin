package com.niikelion.ic10_language.widgets

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidgetHelper
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.logic.Constraints
import com.niikelion.ic10_language.psi.Ic10File
import io.ktor.client.plugins.logging.EMPTY
import org.jetbrains.annotations.NonNls
import java.awt.Component

class Ic10LimitsWidgetFactory: StatusBarEditorBasedWidgetFactory() {
    companion object {
        const val ID = "Ic10.FileLimitsWidget"
    }

    override fun getId() = ID
    override fun getDisplayName() = "Ic10 File Limits Widget"
    override fun createWidget(project: Project): Ic10LimitsWidget = Ic10LimitsWidget(project)
    override fun disposeWidget(widget: StatusBarWidget) = widget.dispose()

    class Ic10LimitsWidget(project: Project):
        EditorBasedStatusBarPopup(project, false),
        Disposable
    {
        override fun ID(): @NonNls String = ID

        private val connection = project.messageBus.connect(this)

        init {
            connection.subscribe(
                FileEditorManagerListener.FILE_EDITOR_MANAGER,
                object : FileEditorManagerListener {
                    override fun selectionChanged(event: FileEditorManagerEvent) {
                        updateForFile(event.newFile)
                    }
                }
            )

            EditorFactory.getInstance().eventMulticaster.addDocumentListener(
                object : DocumentListener {
                    override fun documentChanged(event: DocumentEvent) {
                        updateForDocument(event.document)
                    }
                },
                this
            )
        }

        override fun getWidgetState(file: VirtualFile?): WidgetState {
            if (file == null || file.fileType != Ic10FileType.Instance) return WidgetState.HIDDEN

            val ic10File = file.findDocument()?.let { PsiDocumentManager.getInstance(project).getPsiFile(it) } ?: return WidgetState.HIDDEN

            if (ic10File !is Ic10File) return WidgetState.HIDDEN

            val linesText = "${ic10File.lineCount}/${Constraints.maxLines} lines"
            val bytesText = "${ic10File.byteCount}/${Constraints.maxBytes} bytes"
            val text = arrayOf(linesText, bytesText).joinToString(" ")

            // TODO: eliminate update throttling
            return WidgetState(
                null,
                text,
                false
            )
        }

        override fun createPopup(context: DataContext): ListPopup? = null

        override fun createInstance(project: Project): StatusBarWidget = Ic10LimitsWidget(project)
    }
}