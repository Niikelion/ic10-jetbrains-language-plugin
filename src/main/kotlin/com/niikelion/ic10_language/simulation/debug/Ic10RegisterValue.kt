package com.niikelion.ic10_language.simulation.debug

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.niikelion.ic10_language.utils.toPrettyString

class Ic10RegisterValue(initialValue: Double = 0.0) : XValue() {
    @Volatile private var value: Double = initialValue
    @Volatile private var node: XValueNode? = null

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        this.node = node
        if (node is Disposable) {
            Disposer.register(node) { this.node = null }
        }
        present()
    }

    fun update(newValue: Double) {
        value = newValue
        present()
    }

    private fun present() {
        node?.setPresentation(null, null, value.toPrettyString(), false)
    }
}