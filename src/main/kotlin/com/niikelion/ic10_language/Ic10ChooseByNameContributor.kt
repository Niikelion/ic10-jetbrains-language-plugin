package com.niikelion.ic10_language

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter


class Ic10ChooseByNameContributor: ChooseByNameContributorEx {
    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        val project = scope.project!!
        val files = Ic10PsiUtils.getProjectFiles(project)

        files.forEach { file ->
            Ic10PsiUtils.findDeclarations(file).forEach { declaration ->
                processor.process(declaration.name!!)
            }
        }
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters
    ) {
        val project = parameters.project
        val files = Ic10PsiUtils.getProjectFiles(project)

        files.forEach { file ->
            Ic10PsiUtils.findDeclarations(file).forEach { declaration ->
                processor.process(declaration as NavigationItem)
            }
        }
    }
}