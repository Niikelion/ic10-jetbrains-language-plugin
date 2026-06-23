package com.niikelion.ic10_language.test.logic

import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.logic.ProgramCode
import com.niikelion.ic10_language.psi.Ic10File

class LabelValueTests : BasePlatformTestCase() {

    /**
     * Shadows the package-level [simulate], injecting a PSI-backed compiler so that
     * [InstructionTestBuilder.compile] works inside the block.
     */
    private fun simulate(block: InstructionTestBuilder.() -> Unit) =
        InstructionTestBuilder { code ->
            ProgramCode.compile(
                PsiFileFactory.getInstance(project)
                    .createFileFromText("Test.ic10", Ic10FileType.Instance, code) as Ic10File
            )
        }.also(block).run()

    // label at line 0 → value 0 → move r0 start sets r0 = 0
    fun testLabelAtFirstLineResolvesToZero() {
        simulate {
            compile("""
                >start:
                >move r0 start
            """.trimMargin(">"))
            assert { register("r0", 0.0) }
        }
    }

    // label at line 1 → value 1 → move r0 end (line 0) sets r0 = 1
    fun testLabelAtSecondLineResolvesToOne() {
        simulate {
            compile("""
                >move r0 end
                >end:
            """.trimMargin(">"))
            assert { register("r0", 1.0) }
        }
    }
}
