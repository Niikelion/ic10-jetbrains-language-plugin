package com.niikelion.ic10_language.navigation

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.psi.Ic10File


class Ic10ChooseByNameContributor: ChooseByNameContributorEx {
    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        val project = scope.project!!
        val files = getProjectFiles(project)

        files.forEach { file ->
            Ic10PsiUtils.findDeclarations(file).forEach { declaration ->
                processor.process(declaration.name)
            }
        }
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters
    ) {
        val project = parameters.project
        val files = getProjectFiles(project)

        files.forEach { file ->
            Ic10PsiUtils.findDeclarations(file).forEach { declaration ->
                processor.process(declaration as NavigationItem)
            }
        }
    }

    private fun getProjectFiles(project: Project): List<Ic10File> {
        val virtualFiles = FileTypeIndex.getFiles(Ic10FileType.Instance, GlobalSearchScope.allScope(project))
        val psiManager = PsiManager.getInstance(project)
        return virtualFiles.mapNotNull { file -> psiManager.findFile(file) as Ic10File? }
    }
}