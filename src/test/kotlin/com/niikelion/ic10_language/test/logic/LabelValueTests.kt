package com.niikelion.ic10_language.test.logic

import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.logic.Context
import com.niikelion.ic10_language.logic.Network
import com.niikelion.ic10_language.logic.ProgramCode
import com.niikelion.ic10_language.logic.Registers
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.logic.devices.StructureCircuitHousing
import com.niikelion.ic10_language.psi.Ic10File

class LabelValueTests : BasePlatformTestCase() {

    private fun runOneTick(code: String): Ic10ProgramAspect.State {
        val psiFile = PsiFileFactory.getInstance(project)
            .createFileFromText("Test.ic10", Ic10FileType.Instance, code) as Ic10File

        val programCode = ProgramCode.compile(psiFile)
        val device = StructureCircuitHousing(1L, programCode)
        val context = Context(listOf(device), mapOf(0L to Network.single(setOf(1L))))
        var state = context.initialize()

        for (change in context.step(state)) state = change.perform(state)
        state = context.endTick(state).perform(state)

        return state.devices[1L]!!.aspects[Ic10ProgramAspect.State::class] as Ic10ProgramAspect.State
    }

    private fun r0(state: Ic10ProgramAspect.State) =
        state.registers[Registers.get("r0")!!] ?: error("r0 not found")

    // label at line 0 → value 0 → move r0 start sets r0 = 0
    fun testLabelAtFirstLineResolvesToZero() {
        val state = runOneTick("start:\nmove r0 start")
        assertEquals(0.0, r0(state))
    }

    // label at line 1 → value 1 → move r0 end (line 0) sets r0 = 1
    fun testLabelAtSecondLineResolvesToOne() {
        val state = runOneTick("move r0 end\nend:")
        assertEquals(1.0, r0(state))
    }
}
