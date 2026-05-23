package com.niikelion.ic10_language.simulation.debug

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import com.niikelion.ic10_language.Ic10FileType

class Ic10LineBreakpointType : XLineBreakpointType<XBreakpointProperties<*>>(
    "ic10-line",
    "IC10 Line Breakpoints"
) {
    override fun canPutAt(file: VirtualFile, line: Int, project: Project) =
        file.fileType == Ic10FileType.Instance
    override fun createBreakpointProperties(file: VirtualFile, line: Int): XBreakpointProperties<*>? = null
}
