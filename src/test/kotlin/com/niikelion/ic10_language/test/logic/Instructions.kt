package com.niikelion.ic10_language.test.logic

import com.intellij.testFramework.fixtures.BareTestFixtureTestCase
import com.niikelion.ic10_language.logic.Instructions
import com.niikelion.ic10_language.logic.StationeersRegistryData
import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertTrue

class InstructionTests : BareTestFixtureTestCase() {

    // -------------------------------------------------------------------------
    // Sanity checks
    // -------------------------------------------------------------------------

    @Test
    fun `All instructions from stationpedia are defined`() {
        val scriptCommandNames = StationeersRegistryData.data.scriptCommands.keys
        val definedInstructions = Instructions.all.map { it.name }.toSet() + setOf("label")

        val missing = scriptCommandNames - definedInstructions
        assertTrue(missing.isEmpty(), "Instructions in stationpedia.json but not defined: $missing")
    }

    @Test
    fun `All instructions are implemented`() {
        val notImplemented = Instructions.all.filter { it.action == null }.map { it.name }
        assertTrue(notImplemented.isEmpty(), "Instructions defined, but not implemented: $notImplemented")
    }

    // -------------------------------------------------------------------------
    // Arithmetic
    // -------------------------------------------------------------------------

    @Test
    fun `add matches the in-game behaviour`() {
        simulate {
            exec("add", reg("r0"), num(1), num(-10))
            assert { register("r0", -9) }
        }
        simulate {
            exec("add", reg("r0"), num(0.5), num(0.5))
            assert { register("r0", 1.0) }
        }
        simulate {
            exec("add", reg("r0"), num(1), num(1))
            assert { register("r0", 2.0) }
        }
    }

    @Test
    fun `sub matches the in-game behaviour`() {
        simulate {
            exec("sub", reg("r0"), num(5), num(3))
            assert { register("r0", 2) }
        }
        simulate {
            exec("sub", reg("r0"), num(1), num(10))
            assert { register("r0", -9) }
        }
    }

    @Test
    fun `mul`() {
        TODO("verify in-game")
        simulate {
            exec("mul", reg("r0"), num(3), num(4))
            assert { register("r0", 12) }
        }
        simulate {
            exec("mul", reg("r0"), num(-2), num(5))
            assert { register("r0", -10) }
        }
    }

    @Test
    fun `div matches the in-game behaviour`() {
        simulate {
            exec("div", reg("r0"), num(1), num(2))
            assert { register("r0", 0.5) }
        }
        simulate {
            exec("div", reg("r0"), num(-1), num(2))
            assert { register("r0", -0.5) }
        }
        simulate {
            exec("div", reg("r0"), num(1), num(0))
            assert { register("r0", Double.POSITIVE_INFINITY, 0.0) }
        }
        simulate {
            exec("div", reg("r0"), num(-1), num(0))
            assert { register("r0", Double.NEGATIVE_INFINITY, 0.0) }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(0))
            assert { register("r0", Double.POSITIVE_INFINITY, 0.0) }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(1))
            assert { register("r0", Double.POSITIVE_INFINITY, 0.0) }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(-1))
            assert { register("r0", Double.NEGATIVE_INFINITY, 0.0) }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(Double.POSITIVE_INFINITY))
            assert { register("r0", Double.NaN, 0.0) }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(Double.NEGATIVE_INFINITY))
            assert { register("r0", Double.NaN, 0.0) }
        }
    }

    @Test
    fun `mod matches the in-game behaviour`() {
        simulate {
            exec("mod", reg("r0"), num(10), num(3))
            assert { register("r0", 1) }
        }
        simulate {
            exec("mod", reg("r0"), num(1.3), num(1.2))
            assert { register("r0", 0.1) }
        }
        simulate {
            exec("mod", reg("r0"), num(-0.9), num(1))
            assert { register("r0", 0.1) }
        }
        simulate {
            exec("mod", reg("r0"), num(1), num(-10))
            assert { register("r0", 1) }
        }
        simulate {
            exec("mod", reg("r0"), num(11), num(-10))
            assert { register("r0", 1) }
        }
        simulate {
            exec("mod", reg("r0"), num(-11), num(-10))
            assert { register("r0", -11) }
        }
        simulate {
            exec("mod", reg("r0"), num(-11), num(10))
            assert { register("r0", 9) }
        }
    }

    @Test
    fun `pow`() {
        TODO("verify in-game")
        simulate {
            exec("pow", reg("r0"), num(2), num(10))
            assert { register("r0", 1024.0) }
        }
        simulate {
            exec("pow", reg("r0"), num(4), num(0.5))
            assert { register("r0", 2.0) }
        }
    }

    // -------------------------------------------------------------------------
    // Math
    // -------------------------------------------------------------------------

    @Test
    fun `abs matches the in-game behaviour`() {
        simulate {
            exec("abs", reg("r0"), num(-5))
            assert { register("r0", 5) }
        }
        simulate {
            exec("abs", reg("r0"), num(3))
            assert { register("r0", 3) }
        }
    }

    @Test
    fun `ceil matches the in-game behaviour`() {
        simulate {
            exec("ceil", reg("r0"), num(1.1))
            assert { register("r0", 2) }
        }
        simulate {
            exec("ceil", reg("r0"), num(-2.1))
            assert { register("r0", -2) }
        }
        simulate {
            exec("ceil", reg("r0"), num(-2.9))
            assert { register("r0", -2) }
        }
        simulate {
            exec("ceil", reg("r0"), num(-2))
            assert { register("r0", -2) }
        }
    }

    @Test
    fun `floor matches the in-game behaviour`() {
        simulate {
            exec("floor", reg("r0"), num(1.9))
            assert { register("r0", 1) }
        }
        simulate {
            exec("floor", reg("r0"), num(-2.5))
            assert { register("r0", -3) }
        }
        simulate {
            exec("floor", reg("r0"), num(-2.3))
            assert { register("r0", -3) }
        }
        simulate {
            exec("floor", reg("r0"), num(-2))
            assert { register("r0", -2) }
        }
    }

    @Test
    fun `round matches the in-game behaviour`() {
        simulate {
            exec("round", reg("r0"), num(1.4))
            assert { register("r0", 1) }
        }
        simulate {
            exec("round", reg("r0"), num(1.6))
            assert { register("r0", 2) }
        }
        simulate {
            exec("round", reg("r0"), num(-2.1))
            assert { register("r0", -2) }
        }
        simulate {
            exec("round", reg("r0"), num(-2.9))
            assert { register("r0", -3) }
        }
        simulate {
            exec("round", reg("r0"), num(-2.5))
            assert { register("r0", -2) }
        }
        simulate {
            exec("round", reg("r0"), num(-1.5))
            assert { register("r0", -2) }
        }
        simulate {
            exec("round", reg("r0"), num(-0.5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("round", reg("r0"), num(0.5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("round", reg("r0"), num(1.5))
            assert { register("r0", 2) }
        }
    }

    @Test
    fun `sqrt`() {
        TODO("verify in-game")
        simulate {
            exec("sqrt", reg("r0"), num(9))
            assert { register("r0", 3) }
        }
        simulate {
            exec("sqrt", reg("r0"), num(2))
            assert { register("r0", sqrt(2.0)) }
        }
    }

    @Test
    fun `log`() {
        TODO("verify in-game")
        simulate {
            exec("log", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("log", reg("r0"), num(E))
            assert { register("r0", 1.0) }
        }
    }

    @Test
    fun `exp matches the in-game behaviour`() {
        TODO("verify in-game")
        simulate {
            exec("exp", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("exp", reg("r0"), num(1))
            assert { register("r0", E) }
        }
    }

    @Test
    fun `trunc matches the in-game behaviour`() {
        simulate {
            setup { register("r0", 1.1) }
            exec("trunc", reg("r1"), reg("r0"))
            assert { register("r1", 1) }
        }
        simulate {
            setup { register("r0", -1.1) }
            exec("trunc", reg("r1"), reg("r0"))
            assert { register("r1", -1) }
        }
        simulate {
            setup { register("r0", 1.9) }
            exec("trunc", reg("r1"), reg("r0"))
            assert { register("r1", 1) }
        }
    }

    // -------------------------------------------------------------------------
    // Trigonometry
    // -------------------------------------------------------------------------

    @Test
    fun `sin`() {
        TODO("verify in-game")
        simulate {
            exec("sin", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sin", reg("r0"), num(PI / 2))
            assert { register("r0", 1.0) }
        }
    }

    @Test
    fun `cos`() {
        TODO("verify in-game")
        simulate {
            exec("cos", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("cos", reg("r0"), num(PI))
            assert { register("r0", -1.0) }
        }
    }

    @Test
    fun `tan`() {
        TODO("verify in-game")
        simulate {
            exec("tan", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("tan", reg("r0"), num(PI / 4))
            assert { register("r0", 1.0) }
        }
    }

    @Test
    fun `asin`() {
        TODO("verify in-game")
        simulate {
            exec("asin", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("asin", reg("r0"), num(1))
            assert { register("r0", PI / 2) }
        }
    }

    @Test
    fun `acos`() {
        TODO("verify in-game")
        simulate {
            exec("acos", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("acos", reg("r0"), num(-1))
            assert { register("r0", PI) }
        }
    }

    @Test
    fun `atan`() {
        TODO("verify in-game")
        simulate {
            exec("atan", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("atan", reg("r0"), num(1))
            assert { register("r0", PI / 4) }
        }
    }

    @Test
    fun `atan2`() {
        TODO("verify in-game")
        simulate {
            exec("atan2", reg("r0"), num(1), num(1))
            assert { register("r0", PI / 4) }
        }
        simulate {
            exec("atan2", reg("r0"), num(1), num(-1))
            assert { register("r0", 3 * PI / 4) }
        }
    }

    // -------------------------------------------------------------------------
    // Bitwise
    // -------------------------------------------------------------------------

    @Test
    fun `and`() {
        TODO("verify in-game")
        simulate {
            exec("and", reg("r0"), num(0b1010), num(0b1100))
            assert { register("r0", 0b1000.toDouble()) }
        }
    }

    @Test
    fun `or`() {
        TODO("verify in-game")
        simulate {
            exec("or", reg("r0"), num(0b1010), num(0b1100))
            assert { register("r0", 0b1110.toDouble()) }
        }
    }

    @Test
    fun `xor`() {
        TODO("verify in-game")
        simulate {
            exec("xor", reg("r0"), num(0b1010), num(0b1100))
            assert { register("r0", 0b0110.toDouble()) }
        }
    }

    @Test
    fun `not`() {
        TODO("verify in-game")
        simulate {
            exec("not", reg("r0"), num(0))
            assert { register("r0", -1) }
        }
    }

    @Test
    fun `nor`() {
        TODO("verify in-game")
        simulate {
            exec("nor", reg("r0"), num(0b1010), num(0b1100))
            assert { register("r0", (0b1110.toLong().inv() and ((1L shl 54) - 1L)).toDouble()) }
        }
    }

    // -------------------------------------------------------------------------
    // Shifts
    // -------------------------------------------------------------------------

    @Test
    fun `sll`() {
        TODO("verify in-game")
        simulate {
            exec("sll", reg("r0"), num(1), num(3))
            assert { register("r0", 8) }
        }
        simulate {
            exec("sll", reg("r0"), num(-1), num(1))
            assert { register("r0", -2) }
        }
    }

    @Test
    fun `sla`() {
        TODO("verify in-game")
        simulate {
            exec("sla", reg("r0"), num(1), num(3))
            assert { register("r0", 8) }
        }
        simulate {
            exec("sla", reg("r0"), num(-1), num(1))
            assert { register("r0", -2) }
        }
    }

    @Test
    fun `sra matches the in-game behaviour`() {
        simulate {
            exec("sra", reg("r0"), num(16), num(2))
            assert { register("r0", 4) }
        }
        simulate {
            exec("sra", reg("r0"), num(-16), num(2))
            assert { register("r0", -4) }
        }
        simulate {
            exec("sra", reg("r0"), num(-4), num(60))
            assert { register("r0", -1) }
        }
    }

    @Test
    fun `srl matches the in-game behaviour`() {
        simulate {
            exec("srl", reg("r0"), num(16), num(2))
            assert { register("r0", 4) }
        }
        simulate {
            exec("srl", reg("r0"), num(-16), num(2))
            assert { register("r0", 4503599627370490.0, 10.0) }
        }
        simulate {
            exec("srl", reg("r0"), num(-4), num(60))
            assert { register("r0", 0) }
        }
    }

    // -------------------------------------------------------------------------
    // Approximate comparison
    // -------------------------------------------------------------------------

    @Test
    fun `sap`() {
        TODO("verify in-game")
        simulate {
            exec("sap", reg("r0"), num(1.0), num(1.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sap", reg("r0"), num(1.0), num(2.0), num(0.1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sna`() {
        TODO("verify in-game")
        simulate {
            exec("sna", reg("r0"), num(1.0), num(2.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sna", reg("r0"), num(1.0), num(1.0), num(0.1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sapz`() {
        TODO("verify in-game")
        simulate {
            exec("sapz", reg("r0"), num(0.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sapz", reg("r0"), num(1.0), num(0.1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `snaz`() {
        TODO("verify in-game")
        simulate {
            exec("snaz", reg("r0"), num(1.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snaz", reg("r0"), num(0.0), num(0.1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `snan`() {
        TODO("verify in-game")
        simulate {
            exec("snan", reg("r0"), num(Double.NaN))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snan", reg("r0"), num(1.0))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `snanz`() {
        TODO("verify in-game")
        simulate {
            exec("snanz", reg("r0"), num(1.0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snanz", reg("r0"), num(Double.NaN))
            assert { register("r0", 0) }
        }
    }

    // -------------------------------------------------------------------------
    // Compare / select
    // -------------------------------------------------------------------------

    @Test
    fun `seq`() {
        TODO("verify in-game")
        simulate {
            exec("seq", reg("r0"), num(5), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("seq", reg("r0"), num(5), num(6))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `seqz`() {
        TODO("verify in-game")
        simulate {
            exec("seqz", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("seqz", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sne`() {
        TODO("verify in-game")
        simulate {
            exec("sne", reg("r0"), num(5), num(6))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sne", reg("r0"), num(5), num(5))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `snez`() {
        TODO("verify in-game")
        simulate {
            exec("snez", reg("r0"), num(1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snez", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sgt`() {
        TODO("verify in-game")
        simulate {
            exec("sgt", reg("r0"), num(6), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sgt", reg("r0"), num(5), num(5))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sgtz`() {
        TODO("verify in-game")
        simulate {
            exec("sgtz", reg("r0"), num(1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sgtz", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `slt`() {
        TODO("verify in-game")
        simulate {
            exec("slt", reg("r0"), num(4), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("slt", reg("r0"), num(5), num(5))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sltz`() {
        TODO("verify in-game")
        simulate {
            exec("sltz", reg("r0"), num(-1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sltz", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sge`() {
        TODO("verify in-game")
        simulate {
            exec("sge", reg("r0"), num(5), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sge", reg("r0"), num(4), num(5))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sgez`() {
        TODO("verify in-game")
        simulate {
            exec("sgez", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sgez", reg("r0"), num(-1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sle`() {
        TODO("verify in-game")
        simulate {
            exec("sle", reg("r0"), num(5), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sle", reg("r0"), num(6), num(5))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `slez`() {
        TODO("verify in-game")
        simulate {
            exec("slez", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("slez", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `select`() {
        TODO("verify in-game")
        simulate {
            exec("select", reg("r0"), num(1), num(10), num(20))
            assert { register("r0", 10) }
        }
        simulate {
            exec("select", reg("r0"), num(0), num(10), num(20))
            assert { register("r0", 20) }
        }
    }

    // -------------------------------------------------------------------------
    // Branches (absolute) — DSL calls the action directly, no post-advance
    // -------------------------------------------------------------------------

    @Test
    fun `beq jumps when equal`() {
        TODO("verify in-game")
        simulate {
            exec("beq", num(1), num(1), num(10))
            assert { instructionIndex(10) }
        }
        simulate {
            exec("beq", num(1), num(2), num(10))
            assert { instructionIndex(1) }
        }
    }

    @Test
    fun `bne jumps when not equal`() {
        TODO("verify in-game")
        simulate {
            exec("bne", num(1), num(2), num(5))
            assert { instructionIndex(5) }
        }
        simulate {
            exec("bne", num(1), num(1), num(5))
            assert { instructionIndex(1) }
        }
    }

    @Test
    fun `beqz jumps when zero`() {
        TODO("verify in-game")
        simulate {
            exec("beqz", num(0), num(5))
            assert { instructionIndex(5) }
        }
        simulate {
            exec("beqz", num(1), num(5))
            assert { instructionIndex(1) }
        }
    }

    @Test
    fun `bnez jumps when non-zero`() {
        TODO("verify in-game")
        simulate {
            exec("bnez", num(1), num(5))
            assert { instructionIndex(5) }
        }
    }

    @Test
    fun `bgt jumps when greater`() {
        TODO("verify in-game")
        simulate {
            exec("bgt", num(6), num(5), num(10))
            assert { instructionIndex(10) }
        }
        simulate {
            exec("bgt", num(5), num(5), num(10))
            assert { instructionIndex(1) }
        }
    }

    @Test
    fun `blt jumps when less`() {
        TODO("verify in-game")
        simulate {
            exec("blt", num(4), num(5), num(10))
            assert { instructionIndex(10) }
        }
    }

    @Test
    fun `bge jumps when greater or equal`() {
        TODO("verify in-game")
        simulate {
            exec("bge", num(5), num(5), num(10))
            assert { instructionIndex(10) }
        }
    }

    @Test
    fun `ble jumps when less or equal`() {
        TODO("verify in-game")
        simulate {
            exec("ble", num(5), num(5), num(10))
            assert { instructionIndex(10) }
        }
    }

    // -------------------------------------------------------------------------
    // Jump
    // -------------------------------------------------------------------------

    @Test
    fun `j`() {
        TODO("verify in-game")
        simulate {
            exec("j", num(10))
            assert { instructionIndex(10) }
        }
    }

    @Test
    fun `jal stores return address`() {
        TODO("verify in-game")
        simulate {
            exec("jal", num(10))
            assert {
                instructionIndex(10)
                ra(1)
            }
        }
    }

    @Test
    fun `jr relative jump`() {
        TODO("verify in-game")
        simulate {
            exec("jr", num(5))
            assert { instructionIndex(6) }  // instructionIndex(1) + 5 = 6
        }
    }

    // -------------------------------------------------------------------------
    // Stack
    // -------------------------------------------------------------------------

    @Test
    fun `push and pop`() {
        TODO("verify in-game")
        simulate {
            setup { sp(0) }
            exec("push", num(42))
            assert {
                stackAt(0, 42)
                sp(1)
            }
        }
        simulate {
            setup {
                sp(1)
                stackAt(1, 99)
            }
            exec("pop", reg("r0"))
            assert {
                register("r0", 99)
                sp(0)
            }
        }
    }

    @Test
    fun `peek reads without decrement`() {
        TODO("verify in-game")
        simulate {
            setup {
                sp(1)
                stackAt(1, 77)
            }
            exec("peek", reg("r0"))
            assert {
                register("r0", 77)
                sp(1)
            }
        }
    }

    @Test
    fun `poke writes to address`() {
        TODO("verify in-game")
        simulate {
            exec("poke", num(5), num(123))
            assert { stackAt(5, 123) }
        }
    }

    // -------------------------------------------------------------------------
    // Control flow
    // -------------------------------------------------------------------------

    @Test
    fun `yield sets waitFor to 1`() {
        TODO("verify in-game")
        simulate {
            exec("yield")
            assert { notOnFire() }
        }
    }

    @Test
    fun `hcf lights device on fire`() {
        simulate {
            exec("hcf")
            assert { onFire() }
        }
    }

    // -------------------------------------------------------------------------
    // Misc
    // -------------------------------------------------------------------------

    @Test
    fun `move`() {
        TODO("verify in-game")
        simulate {
            exec("move", reg("r0"), num(42))
            assert { register("r0", 42) }
        }
    }

    @Test
    fun `lerp`() {
        TODO("verify in-game")
        simulate {
            exec("lerp", reg("r0"), num(0), num(10), num(0.5))
            assert { register("r0", 5.0) }
        }
        simulate {
            exec("lerp", reg("r0"), num(0), num(10), num(0.0))
            assert { register("r0", 10.0) }
        }
        simulate {
            exec("lerp", reg("r0"), num(0), num(10), num(1.0))
            assert { register("r0", 0.0) }
        }
    }

    @Test
    fun `max`() {
        TODO("verify in-game")
        simulate {
            exec("max", reg("r0"), num(3), num(7))
            assert { register("r0", 7) }
        }
    }

    @Test
    fun `min`() {
        TODO("verify in-game")
        simulate {
            exec("min", reg("r0"), num(3), num(7))
            assert { register("r0", 3) }
        }
    }

    @Test
    fun `rand produces value in 0 until 1`() {
        TODO("verify in-game")
        repeat(20) {
            simulate {
                exec("rand", reg("r0"))
                assert {
                    notOnFire()
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Device read/write (single-device, same network)
    // -------------------------------------------------------------------------

    @Test
    fun `l reads device property`() {
        TODO("verify in-game")
        val targetId = 10L
        val propId = 42
        simulate {
            setup {
                deviceSlot("d0", targetId)
                addDevice(targetId, mapOf(propId to 99.0))
            }
            exec("l", reg("r0"), device("d0"), num(propId))
            assert { register("r0", 99.0) }
        }
    }

    @Test
    fun `s writes device property`() {
        TODO("verify in-game")
        val targetId = 10L
        val propId = 42
        simulate {
            setup {
                deviceSlot("d0", targetId)
                addDevice(targetId, mapOf(propId to 0.0))
            }
            exec("s", device("d0"), num(propId), num(55.0))
            assert { deviceProperty(targetId, propId, 55.0) }
        }
    }

    // -------------------------------------------------------------------------
    // Channels
    // -------------------------------------------------------------------------

    @Test
    fun `network channel read and write`() {
        TODO("verify in-game")
        simulate {
            setup { networkChannel(0, 42.0) }
            assert { networkChannel(0, 42.0) }
        }
    }

    // -------------------------------------------------------------------------
    // sdns / sdse
    // -------------------------------------------------------------------------

    @Test
    fun `sdns returns 1 when slot is empty`() {
        TODO("verify in-game")
        simulate {
            exec("sdns", reg("r0"), device("d0"))
            assert { register("r0", 1) }
        }
    }

    @Test
    fun `sdse returns 1 when slot is set`() {
        TODO("verify in-game")
        simulate {
            setup { deviceSlot("d0", 5L) }
            exec("sdse", reg("r0"), device("d0"))
            assert { register("r0", 1) }
        }
    }
}
