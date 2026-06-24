package com.niikelion.ic10_language.test.logic

import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.logic.ProgramCode
import com.niikelion.ic10_language.psi.Ic10File

/** A halted device keeps its instruction pointer on the offending line so the debugger can point at it. */
class RuntimeErrorPositionTests : BasePlatformTestCase() {

    private fun simulate(block: InstructionTestBuilder.() -> Unit) =
        InstructionTestBuilder { code ->
            ProgramCode.compile(
                PsiFileFactory.getInstance(project)
                    .createFileFromText("Test.ic10", Ic10FileType.Instance, code) as Ic10File
            )
        }.also(block).run()

    fun testFaultingInstructionHaltsAtItsLine() {
        simulate {
            setup { deviceSlot("d0", 99L) }
            compile("""
                >move r0 1
                >l r1 d0 Setting
            """.trimMargin(">"))
            assert {
                hasError()
                instructionIndex(1)
            }
        }
    }

    fun testHcfHaltsAtItsLine() {
        simulate {
            compile("""
                >move r0 1
                >hcf
            """.trimMargin(">"))
            assert {
                onFire()
                instructionIndex(1)
            }
        }
    }
}
