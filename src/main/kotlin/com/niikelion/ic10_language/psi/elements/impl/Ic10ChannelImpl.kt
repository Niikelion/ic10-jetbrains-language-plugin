package com.niikelion.ic10_language.psi.elements.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.niikelion.ic10_language.logic.ChannelValue
import com.niikelion.ic10_language.logic.DeviceSlots
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.psi.Ic10Channel
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement

open class Ic10ChannelImpl(node: ASTNode): ASTWrapperPsiElement(node), Ic10ValueLikeElement {
    override fun getValue(): IUnresolvedValue? {
        val channel = this as? Ic10Channel ?: return null
        val slot = DeviceSlots.get(channel.referenceName.text) ?: return null
        val index = channel.channelNumber.text.toIntOrNull() ?: return null
        return ChannelValue(slot, index)
    }
}
