package com.niikelion.ic10_language.simulation.debug

import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.frame.XExecutionStack
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.simulation.SimulationProcess

class Ic10ExecutionStack(
    private val device: Device,
    private val process: SimulationProcess
) : XExecutionStack(device.name) {

    // Stable instance — same object returned on every call so rebuildViews() can preserve tree state
    private val frame = Ic10StackFrame(device, process) {
        val deviceState = process.state.value.devices[device.id] ?: return@Ic10StackFrame null
        val programAspect = device.aspect<Ic10ProgramAspect>() ?: return@Ic10StackFrame null
        val programState = deviceState.aspect<Ic10ProgramAspect.State>() ?: return@Ic10StackFrame null
        val sourceLine = programAspect.code.lines.getOrNull(programState.instructionIndex)
            ?.sourceLine?.takeIf { it >= 0 } ?: return@Ic10StackFrame null
        val virtualFile = programAspect.code.source.virtualFile ?: return@Ic10StackFrame null
        XDebuggerUtil.getInstance().createPosition(virtualFile, sourceLine)
    }

    override fun getTopFrame() = frame

    override fun computeStackFrames(firstFrameIndex: Int, container: XStackFrameContainer) {
        container.addStackFrames(listOf(frame), true)
    }
}
