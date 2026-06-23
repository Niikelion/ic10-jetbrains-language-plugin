package com.niikelion.ic10_language.simulation.debug

import com.intellij.icons.AllIcons
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XValueChildrenList
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.simulation.SimulationProcess

class Ic10StackFrame(
    private val device: Device,
    private val process: SimulationProcess,
    private val resolvePosition: () -> XSourcePosition?
) : XStackFrame() {
    private val propertiesGroup = Ic10GroupValue(device.name)
    private val aspectGroups = device.aspects.map { entry ->
        val aspect = entry.value
        aspect.name to Ic10GroupValue(
            label = aspect.debuggerLabel,
            type = aspect.debuggerType,
            tableTitle = if (aspect.debuggerTableView) aspect.name else null
        )
    }

    override fun getEqualityObject() = device

    override fun getSourcePosition(): XSourcePosition? = resolvePosition()

    override fun computeChildren(node: XCompositeNode) {
        val deviceState = process.state.value.devices[device.id] ?: return
        val list = XValueChildrenList()

        device.aspects.firstNotNullOfOrNull { aspectEntry ->
            deviceState.aspects[aspectEntry.value.stateClass]?.debuggerStatus()
        }?.let { status ->
            list.add("Status", Ic10TextValue(status, AllIcons.General.Error))
        }

        val propEntries = device.properties.mapNotNull { (propId, propDef) ->
            deviceState.properties[propId]?.let { propDef.name to it }
        }
        propertiesGroup.updateEntries(propEntries)
        if (propEntries.isNotEmpty()) list.add("Properties", propertiesGroup)

        device.aspects.forEachIndexed { i, aspectEntry ->
            val state = deviceState.aspects[aspectEntry.value.stateClass] ?: return@forEachIndexed
            val entries = state.debuggerEntries()
            aspectGroups[i].second.updateEntries(entries)
            if (entries.isNotEmpty()) list.add(aspectGroups[i].first, aspectGroups[i].second)
        }

        node.addChildren(list, true)
    }
}
