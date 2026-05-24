package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.niikelion.ic10_language.logic.NetworkRefValue
import com.niikelion.ic10_language.logic.DeviceSlots
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.psi.Ic10NetworkRef
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement

open class Ic10NetworkRefImpl(node: ASTNode): ASTWrapperPsiElement(node), Ic10ValueLikeElement {
    override fun getValue(): IUnresolvedValue? {
        val networkRef = this as? Ic10NetworkRef ?: return null
        val slot = DeviceSlots.get(networkRef.referenceName.text) ?: return null
        val index = networkRef.portIndex.text.toIntOrNull() ?: return null
        return NetworkRefValue(slot, index)
    }
}
