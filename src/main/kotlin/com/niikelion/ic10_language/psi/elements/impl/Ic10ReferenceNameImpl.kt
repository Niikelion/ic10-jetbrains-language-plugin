package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.util.findParentOfType
import com.niikelion.ic10_language.logic.DeviceReferenceValue
import com.niikelion.ic10_language.logic.Entity
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.logic.RegisterReferenceValue
import com.niikelion.ic10_language.psi.Ic10File
import com.niikelion.ic10_language.psi.Ic10Operation
import com.niikelion.ic10_language.psi.NamedElementPresentation
import com.niikelion.ic10_language.psi.elements.Ic10ReferenceNameElement

open class Ic10ReferenceNameImpl(node: ASTNode) : Ic10NamedElementImpl(node), Ic10ReferenceNameElement {
    override fun getValue(): IUnresolvedValue? = when (val file = containingFile) {
        is Ic10File -> file.findEntity(name)?.targetValue
            ?: RegisterReferenceValue.fromString(name)
            ?: DeviceReferenceValue.fromString(name)
        else -> null
    }

    override fun getPresentation(): ItemPresentation? {
        val operation = findParentOfType<Ic10Operation>() ?: return null
        if (operation.declarationToken != this) return null

        return NamedElementPresentation(this)
    }

    override val entity: Entity? get() = findParentOfType<Ic10Operation>()?.let(Entity::from)
}