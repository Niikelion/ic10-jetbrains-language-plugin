package com.niikelion.ic10_language.simulation.debug

import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class Ic10BreakpointHandler(
    private val addBreakpoint: (String, Int) -> Unit,
    private val removeBreakpoint: (String, Int) -> Unit
) : XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(
    Ic10LineBreakpointType::class.java
) {
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        addBreakpoint(breakpoint.fileUrl, breakpoint.line)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>, temporary: Boolean) {
        removeBreakpoint(breakpoint.fileUrl, breakpoint.line)
    }
}
