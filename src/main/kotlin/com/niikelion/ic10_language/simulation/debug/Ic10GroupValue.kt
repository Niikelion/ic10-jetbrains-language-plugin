package com.niikelion.ic10_language.simulation.debug

import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace

class Ic10GroupValue(
    private val label: String = "",
    private val type: String? = null,
    private val tableTitle: String? = null
) : XValue() {
    private val children = LinkedHashMap<String, Ic10RegisterValue>()
    private val currentData = LinkedHashMap<String, Double>()

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        node.setPresentation(null, type, label, true)
        tableTitle?.let { node.setFullValueEvaluator(Ic10ArrayTableEvaluator(it) { currentData.toList() }) }
    }

    override fun computeChildren(node: XCompositeNode) {
        val list = XValueChildrenList()
        children.forEach { (name, value) -> list.add(name, value) }
        node.addChildren(list, true)
    }

    fun updateEntries(entries: List<Pair<String, Double>>) {
        val incomingNames = entries.mapTo(HashSet()) { it.first }
        children.keys.retainAll(incomingNames)
        currentData.keys.retainAll(incomingNames)
        entries.forEach { (name, value) ->
            children.getOrPut(name) { Ic10RegisterValue() }.update(value)
            currentData[name] = value
        }
    }
}