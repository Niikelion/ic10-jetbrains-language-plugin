package com.niikelion.ic10_language.simulation.debug

import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import javax.swing.Icon

/** A read-only debugger value that presents a plain text string (e.g. a halt reason). */
class Ic10TextValue(private val text: String, private val icon: Icon? = null) : XValue() {
    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        node.setPresentation(icon, null, text, false)
    }
}
