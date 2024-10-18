package com.niikelion.ic10_language.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.niikelion.ic10_language.Ic10Language

object Ic10ElementFactory {
    fun createLabel(project: Project, name: String): Ic10Label? {
        val file = createFile(project, "$name:")
        val line = file.findChildByClass(Ic10Line::class.java)

        return line?.label
    }

    private fun createFile(project: Project, text: String): Ic10File {
        return PsiFileFactory
            .getInstance(project)
            .createFileFromText("tmp.${Ic10Language.Instance.associatedFileType?.name}", Ic10Language.Instance, text) as Ic10File
    }
}