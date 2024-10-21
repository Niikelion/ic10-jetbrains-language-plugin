package com.niikelion.ic10_language

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiFile
import com.niikelion.ic10_language.psi.Ic10File
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.psi.impl.Ic10LabelImpl

class Ic10StructureViewFactory: PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder {
        return object: TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return Ic10StructureViewModel(editor!!, psiFile)
            }
        }
    }
}

class Ic10StructureViewModel(editor: Editor, file: PsiFile): StructureViewModelBase(file, editor, Ic10StructureViewElement(file)), StructureViewModel.ElementInfoProvider {
    override fun getSorters(): Array<Sorter> = arrayOf(Sorter.ALPHA_SORTER)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean = element.value is Ic10Label

    override fun getSuitableClasses(): Array<Class<*>> = arrayOf(Ic10Label::class.java)
}

class Ic10StructureViewElement(private val element: NavigatablePsiElement): StructureViewTreeElement, SortableTreeElement {
    override fun getPresentation(): ItemPresentation = element.presentation ?: PresentationData()

    override fun getChildren(): Array<TreeElement> {
        if (element !is Ic10File) return StructureViewTreeElement.EMPTY_ARRAY.map { it!! }.toTypedArray()

        val symbols = Ic10PsiUtils.findLabelsInFile(element)
        return symbols.map { Ic10StructureViewElement(it as Ic10LabelImpl) }.toTypedArray()
    }

    override fun getValue(): Any = element

    override fun getAlphaSortKey(): String = element.name ?: ""

    override fun navigate(requestFocus: Boolean) = element.navigate(requestFocus)
    override fun canNavigate(): Boolean = element.canNavigate()
    override fun canNavigateToSource(): Boolean = element.canNavigateToSource()

}