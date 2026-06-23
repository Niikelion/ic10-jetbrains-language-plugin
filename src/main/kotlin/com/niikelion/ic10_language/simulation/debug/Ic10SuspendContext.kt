package com.niikelion.ic10_language.simulation.debug

import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XSuspendContext
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.simulation.SimulationProcess

class Ic10SuspendContext(
    process: SimulationProcess,
    devices: List<Device>
) : XSuspendContext() {
    // Stable stacks — one per device, so the variable panel can show each device independently
    // and so the frames dropdown lets the user switch between them.
    private val stacks = devices.map { Ic10ExecutionStack(it, process) }
    private val stacksByDevice = stacks.associateBy { it.device }

    // The stack shown by default when the session suspends. Points at the device that hit the
    // breakpoint so focus follows it instead of always landing on the first device.
    @Volatile private var activeStack: Ic10ExecutionStack? = stacks.firstOrNull()

    fun setActiveDevice(device: Device) {
        stacksByDevice[device]?.let { activeStack = it }
    }

    override fun getActiveExecutionStack(): XExecutionStack? = activeStack

    override fun getExecutionStacks(): Array<out XExecutionStack?> = stacks.toTypedArray()
}
