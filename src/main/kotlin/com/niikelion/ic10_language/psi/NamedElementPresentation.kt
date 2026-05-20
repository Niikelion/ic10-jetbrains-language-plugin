package com.niikelion.ic10_language.psi

import com.intellij.navigation.ItemPresentation
import com.niikelion.ic10_language.psi.elements.Ic10NamedElement
import javax.swing.Icon

class NamedElementPresentation(val element: Ic10NamedElement) : ItemPresentation {
    override fun getPresentableText(): String = element.name

    override fun getLocationString(): String? = element.containingFile?.name

    override fun getIcon(unused: Boolean): Icon = element.getIcon(0)
}