package com.niikelion.ic10_language.simulation.debug

import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XSuspendContext
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.simulation.SimulationProcess

class Ic10SuspendContext(
    process: SimulationProcess,
    devices: List<Device>
) : XSuspendContext() {
    private val stacks = devices.map { Ic10ExecutionStack(it, process) }

    override fun getActiveExecutionStack(): XExecutionStack? = stacks.firstOrNull()

    override fun getExecutionStacks(): Array<out XExecutionStack?> = stacks.toTypedArray()


}
