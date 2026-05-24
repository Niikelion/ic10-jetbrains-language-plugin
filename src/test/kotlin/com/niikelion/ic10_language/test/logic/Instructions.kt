package com.niikelion.ic10_language.test.logic

import com.intellij.testFramework.fixtures.BareTestFixtureTestCase
import com.niikelion.ic10_language.logic.Instructions
import com.niikelion.ic10_language.logic.Network
import com.niikelion.ic10_language.logic.NetworkContext
import com.niikelion.ic10_language.logic.StationeersRegistryData
import com.niikelion.ic10_language.logic.state.NetworkState
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder
import com.niikelion.ic10_language.logic.devices.DeviceState
import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertNotNull
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
    fun `mul matches the in-game behaviour`() {
        simulate {
            exec("mul", reg("r0"), num(3), num(4))
            assert { register("r0", 12) }
        }
        simulate {
            exec("mul", reg("r0"), num(-2), num(5))
            assert { register("r0", -10) }
        }
        simulate {
            exec("mul", reg("r0"), num(-3), num(-4))
            assert { register("r0", 12) }
        }
        simulate {
            exec("mul", reg("r0"), num(0.5), num(4))
            assert { register("r0", 2.0) }
        }
        simulate {
            exec("mul", reg("r0"), num(0), num(99))
            assert { register("r0", 0) }
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
    fun `pow matches the in-game behaviour`() {
        simulate {
            exec("pow", reg("r0"), num(2), num(10))
            assert { register("r0", 1024.0) }
        }
        simulate {
            exec("pow", reg("r0"), num(4), num(0.5))
            assert { register("r0", 2.0) }
        }
        simulate {
            exec("pow", reg("r0"), num(2), num(0))
            assert { register("r0", 1.0) }
        }
        simulate {
            exec("pow", reg("r0"), num(2), num(-1))
            assert { register("r0", 0.5) }
        }
        simulate {
            exec("pow", reg("r0"), num(-2), num(3))
            assert { register("r0", -8.0) }
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
    fun `sqrt matches the in-game behaviour`() {
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
    fun `log matches the in-game behaviour`() {
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
    fun `sin matches the in-game behaviour`() {
        simulate {
            exec("sin", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sin", reg("r0"), num(PI / 2))
            assert { register("r0", 1.0) }
        }
        simulate {
            exec("sin", reg("r0"), num(PI))
            assert { register("r0", sin(PI)) }
        }
        simulate {
            exec("sin", reg("r0"), num(3 * PI / 2))
            assert { register("r0", -1.0) }
        }
        simulate {
            exec("sin", reg("r0"), num(-PI / 2))
            assert { register("r0", -1.0) }
        }
    }

    @Test
    fun `cos matches the in-game behaviour`() {
        simulate {
            exec("cos", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("cos", reg("r0"), num(PI))
            assert { register("r0", -1.0) }
        }
        simulate {
            exec("cos", reg("r0"), num(PI / 2))
            assert { register("r0", cos(PI / 2)) }
        }
        simulate {
            exec("cos", reg("r0"), num(2 * PI))
            assert { register("r0", cos(2 * PI)) }
        }
        simulate {
            exec("cos", reg("r0"), num(-PI))
            assert { register("r0", -1.0) }
        }
    }

    @Test
    fun `tan matches the in-game behaviour`() {
        simulate {
            exec("tan", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("tan", reg("r0"), num(PI / 4))
            assert { register("r0", 1.0) }
        }
        simulate {
            exec("tan", reg("r0"), num(-PI / 4))
            assert { register("r0", -1.0) }
        }
        simulate {
            exec("tan", reg("r0"), num(PI / 6))
            assert { register("r0", tan(PI / 6)) }
        }
    }

    @Test
    fun `asin matches the in-game behaviour`() {
        simulate {
            exec("asin", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("asin", reg("r0"), num(1))
            assert { register("r0", PI / 2) }
        }
        simulate {
            exec("asin", reg("r0"), num(-1))
            assert { register("r0", -PI / 2) }
        }
        simulate {
            exec("asin", reg("r0"), num(0.5))
            assert { register("r0", asin(0.5)) }
        }
    }

    @Test
    fun `acos matches the in-game behaviour`() {
        simulate {
            exec("acos", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("acos", reg("r0"), num(-1))
            assert { register("r0", PI) }
        }
        simulate {
            exec("acos", reg("r0"), num(0))
            assert { register("r0", PI / 2) }
        }
        simulate {
            exec("acos", reg("r0"), num(0.5))
            assert { register("r0", acos(0.5)) }
        }
    }

    @Test
    fun `atan matches the in-game behaviour`() {
        simulate {
            exec("atan", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("atan", reg("r0"), num(1))
            assert { register("r0", PI / 4) }
        }
        simulate {
            exec("atan", reg("r0"), num(-1))
            assert { register("r0", -PI / 4) }
        }
        simulate {
            exec("atan", reg("r0"), num(Double.POSITIVE_INFINITY))
            assert { register("r0", PI / 2) }
        }
    }

    @Test
    fun `atan2 matches the in-game behaviour`() {
        simulate {
            exec("atan2", reg("r0"), num(1), num(1))
            assert { register("r0", PI / 4) }
        }
        simulate {
            exec("atan2", reg("r0"), num(1), num(-1))
            assert { register("r0", 3 * PI / 4) }
        }
        simulate {
            exec("atan2", reg("r0"), num(-1), num(1))
            assert { register("r0", -PI / 4) }
        }
        simulate {
            exec("atan2", reg("r0"), num(1), num(0))
            assert { register("r0", PI / 2) }
        }
        simulate {
            exec("atan2", reg("r0"), num(0), num(1))
            assert { register("r0", 0) }
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
            assert { register("r0", 0b1110.toLong().inv().toDouble()) }  // nor(a,b) = ~(a|b) in signed 64-bit
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
    fun `sap matches the in-game behaviour`() {
        simulate {
            exec("sap", reg("r0"), num(1.0), num(1.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sap", reg("r0"), num(1.0), num(2.0), num(0.1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sap", reg("r0"), num(1.0), num(1.05), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            // relative tolerance: |10| <= 0.1 * max(100, 110) = 11
            exec("sap", reg("r0"), num(100.0), num(110.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            // both zero: diff is 0, always within epsilon*8 minimum threshold
            exec("sap", reg("r0"), num(0.0), num(0.0), num(0.1))
            assert { register("r0", 1) }
        }
    }

    @Test
    fun `sna matches the in-game behaviour`() {
        simulate {
            exec("sna", reg("r0"), num(1.0), num(2.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sna", reg("r0"), num(1.0), num(1.0), num(0.1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sna", reg("r0"), num(1.0), num(1.05), num(0.1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sna", reg("r0"), num(100.0), num(200.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sna", reg("r0"), num(0.0), num(0.0), num(0.1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sapz matches the in-game behaviour`() {
        simulate {
            exec("sapz", reg("r0"), num(0.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sapz", reg("r0"), num(1.0), num(0.1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sapz", reg("r0"), num(-1.0), num(0.1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `snaz matches the in-game behaviour`() {
        simulate {
            exec("snaz", reg("r0"), num(1.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snaz", reg("r0"), num(-1.0), num(0.1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snaz", reg("r0"), num(0.0), num(0.1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `snan matches the in-game behaviour`() {
        simulate {
            exec("snan", reg("r0"), num(Double.NaN))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snan", reg("r0"), num(1.0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("snan", reg("r0"), num(0.0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("snan", reg("r0"), num(Double.POSITIVE_INFINITY))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `snanz matches the in-game behaviour`() {
        simulate {
            exec("snanz", reg("r0"), num(1.0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snanz", reg("r0"), num(Double.NaN))
            assert { register("r0", 0) }
        }
        simulate {
            exec("snanz", reg("r0"), num(0.0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snanz", reg("r0"), num(Double.POSITIVE_INFINITY))
            assert { register("r0", 1) }
        }
    }

    // -------------------------------------------------------------------------
    // Compare / select
    // -------------------------------------------------------------------------

    @Test
    fun `seq matches the in-game behaviour`() {
        simulate {
            exec("seq", reg("r0"), num(5), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("seq", reg("r0"), num(5), num(6))
            assert { register("r0", 0) }
        }
        simulate {
            exec("seq", reg("r0"), num(0), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            // IEEE 754: NaN != NaN
            exec("seq", reg("r0"), num(Double.NaN), num(Double.NaN))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `seqz matches the in-game behaviour`() {
        simulate {
            exec("seqz", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("seqz", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
        simulate {
            exec("seqz", reg("r0"), num(-1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sne matches the in-game behaviour`() {
        simulate {
            exec("sne", reg("r0"), num(5), num(6))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sne", reg("r0"), num(5), num(5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sne", reg("r0"), num(-1), num(1))
            assert { register("r0", 1) }
        }
        simulate {
            // IEEE 754: NaN != NaN
            exec("sne", reg("r0"), num(Double.NaN), num(Double.NaN))
            assert { register("r0", 1) }
        }
    }

    @Test
    fun `snez matches the in-game behaviour`() {
        simulate {
            exec("snez", reg("r0"), num(1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snez", reg("r0"), num(-1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("snez", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sgt matches the in-game behaviour`() {
        simulate {
            exec("sgt", reg("r0"), num(6), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sgt", reg("r0"), num(5), num(5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sgt", reg("r0"), num(4), num(5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sgt", reg("r0"), num(-1), num(-2))
            assert { register("r0", 1) }
        }
    }

    @Test
    fun `sgtz matches the in-game behaviour`() {
        simulate {
            exec("sgtz", reg("r0"), num(1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sgtz", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sgtz", reg("r0"), num(-1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `slt matches the in-game behaviour`() {
        simulate {
            exec("slt", reg("r0"), num(4), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("slt", reg("r0"), num(5), num(5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("slt", reg("r0"), num(6), num(5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("slt", reg("r0"), num(-2), num(-1))
            assert { register("r0", 1) }
        }
    }

    @Test
    fun `sltz matches the in-game behaviour`() {
        simulate {
            exec("sltz", reg("r0"), num(-1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sltz", reg("r0"), num(0))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sltz", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sge matches the in-game behaviour`() {
        simulate {
            exec("sge", reg("r0"), num(5), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sge", reg("r0"), num(6), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sge", reg("r0"), num(4), num(5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sge", reg("r0"), num(-1), num(-2))
            assert { register("r0", 1) }
        }
    }

    @Test
    fun `sgez matches the in-game behaviour`() {
        simulate {
            exec("sgez", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sgez", reg("r0"), num(1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sgez", reg("r0"), num(-1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `sle matches the in-game behaviour`() {
        simulate {
            exec("sle", reg("r0"), num(5), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sle", reg("r0"), num(4), num(5))
            assert { register("r0", 1) }
        }
        simulate {
            exec("sle", reg("r0"), num(6), num(5))
            assert { register("r0", 0) }
        }
        simulate {
            exec("sle", reg("r0"), num(-2), num(-1))
            assert { register("r0", 1) }
        }
    }

    @Test
    fun `slez matches the in-game behaviour`() {
        simulate {
            exec("slez", reg("r0"), num(0))
            assert { register("r0", 1) }
        }
        simulate {
            exec("slez", reg("r0"), num(-1))
            assert { register("r0", 1) }
        }
        simulate {
            exec("slez", reg("r0"), num(1))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `select matches the in-game behaviour`() {
        simulate {
            exec("select", reg("r0"), num(1), num(10), num(20))
            assert { register("r0", 10) }
        }
        simulate {
            exec("select", reg("r0"), num(0), num(10), num(20))
            assert { register("r0", 20) }
        }
        simulate {
            exec("select", reg("r0"), num(5), num(10), num(20))
            assert { register("r0", 10) }
        }
        simulate {
            // negative is non-zero, so picks b
            exec("select", reg("r0"), num(-1), num(10), num(20))
            assert { register("r0", 10) }
        }
    }

    // -------------------------------------------------------------------------
    // Branches (absolute) â€” DSL calls the action directly, no post-advance
    // -------------------------------------------------------------------------

    @Test
    fun `beq jumps when equal`() {

        simulate {
            exec("beq", num(1), num(1), num(10))
            assert { instructionIndex(10) }
        }
        simulate {
            exec("beq", num(1), num(2), num(10))
            assert { instructionIndex(0) }  // no jump, index unchanged
        }
    }

    @Test
    fun `bne jumps when not equal`() {

        simulate {
            exec("bne", num(1), num(2), num(5))
            assert { instructionIndex(5) }
        }
        simulate {
            exec("bne", num(1), num(1), num(5))
            assert { instructionIndex(0) }  // no jump, index unchanged
        }
    }

    @Test
    fun `beqz jumps when zero`() {

        simulate {
            exec("beqz", num(0), num(5))
            assert { instructionIndex(5) }
        }
        simulate {
            exec("beqz", num(1), num(5))
            assert { instructionIndex(0) }  // no jump, index unchanged
        }
    }

    @Test
    fun `bnez jumps when non-zero`() {

        simulate {
            exec("bnez", num(1), num(5))
            assert { instructionIndex(5) }
        }
    }

    @Test
    fun `bgt jumps when greater`() {

        simulate {
            exec("bgt", num(6), num(5), num(10))
            assert { instructionIndex(10) }
        }
        simulate {
            exec("bgt", num(5), num(5), num(10))
            assert { instructionIndex(0) }  // no jump, index unchanged
        }
    }

    @Test
    fun `blt jumps when less`() {

        simulate {
            exec("blt", num(4), num(5), num(10))
            assert { instructionIndex(10) }
        }
    }

    @Test
    fun `bge jumps when greater or equal`() {

        simulate {
            exec("bge", num(5), num(5), num(10))
            assert { instructionIndex(10) }
        }
    }

    @Test
    fun `ble jumps when less or equal`() {

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

        simulate {
            exec("j", num(10))
            assert { instructionIndex(10) }
        }
    }

    @Test
    fun `jal stores return address`() {

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

        simulate {
            exec("jr", num(5))
            assert { instructionIndex(5) }  // initial instructionIndex(0) + 5 = 5
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
        simulate {
            exec("yield")
            assert { waitingFor(1) }
        }
    }

    @Test
    fun `sleep matches the in-game behaviour`() {
        simulate {
            // 1s * 2 ticks/s = 2
            exec("sleep", num(1.0))
            assert { waitingFor(2) }
        }
        simulate {
            // 0.5s * 2 = 1
            exec("sleep", num(0.5))
            assert { waitingFor(1) }
        }
        simulate {
            // ceil(0.3 * 2) = ceil(0.6) = 1
            exec("sleep", num(0.3))
            assert { waitingFor(1) }
        }
        simulate {
            // negative time is clamped to 0
            exec("sleep", num(-1.0))
            assert { waitingFor(0) }
        }
        simulate {
            exec("sleep", num(0.0))
            assert { waitingFor(0) }
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
    fun `move matches in-game behaviour`() {
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

    // -------------------------------------------------------------------------
    // Network semantics: softConnected vs dataConnected
    // -------------------------------------------------------------------------

    @Test
    fun `channel read works for dataConnected device`() {
        val targetId = 20L
        simulate {
            setup {
                deviceSlot("d0", targetId)
                addDevice(targetId, emptyMap(), dataConnected = true)
                networkChannel(0, 7.0)
            }
            // l r0 d0:0 Channel0
            exec("l", reg("r0"), channel("d0"), channelType(0))
            assert { register("r0", 7.0) }
        }
    }

    @Test
    fun `channel read works for softConnected device on same network`() {
        val targetId = 21L
        simulate {
            setup {
                deviceSlot("d0", targetId)
                addDevice(targetId, emptyMap(), dataConnected = false)
                networkChannel(0, 5.0)
            }
            // l r0 d0:0 Channel0
            exec("l", reg("r0"), channel("d0"), channelType(0))
            assert { register("r0", 5.0) }
        }
    }

    @Test
    fun `channel write works for softConnected device on same network`() {
        val targetId = 22L
        simulate {
            setup {
                deviceSlot("d0", targetId)
                addDevice(targetId, emptyMap(), dataConnected = false)
            }
            // s d0:0 Channel2 99
            exec("s", channel("d0"), channelType(2), num(99.0))
            assert { networkChannel(2, 99.0) }
        }
    }

    @Test
    fun `property read is blocked for softConnected device`() {
        val targetId = 23L
        val propId = 42
        simulate {
            setup {
                deviceSlot("d0", targetId)
                addDevice(targetId, mapOf(propId to 1.0), dataConnected = false)
            }
            execFails("l", reg("r0"), device("d0"), num(propId))
        }
    }

    @Test
    fun `property write is blocked for softConnected device`() {
        val targetId = 24L
        val propId = 42
        simulate {
            setup {
                deviceSlot("d0", targetId)
                addDevice(targetId, mapOf(propId to 0.0), dataConnected = false)
            }
            execFails("s", device("d0"), num(propId), num(1.0))
        }
    }

    @Test
    fun `channel write via db slot writes to own network`() {
        // s db:0 Channel2 4 â€” db holds the IC10's own device ID; db:0 identifies the
        // IC10's first (only) data network; Channel2 is the channel; 4 is the value.
        simulate {
            setup { /* db slot is pre-initialised to the IC10's own device ID (0L) */ }
            exec("s", channel("db"), channelType(2), num(4.0))
            assert { networkChannel(2, 4.0) }
        }
    }

    @Test
    fun `channelsOf returns null for device on a different network`() {
        val observerId = 0L
        val foreignId = 99L
        val observerNetwork = Network(dataConnected = setOf(observerId), softConnected = emptySet())
        val foreignNetwork = Network(dataConnected = setOf(foreignId), softConnected = emptySet())
        val deviceNetworks = mapOf(
            observerId to Pair(0L, observerNetwork),
            foreignId  to Pair(1L, foreignNetwork)
        )
        val state = SimulationState(
            devices  = mapOf(observerId to DeviceState(emptyMap(), emptyMap()),
                             foreignId  to DeviceState(emptyMap(), emptyMap())),
            networks = mapOf(0L to NetworkState(), 1L to NetworkState())
        )
        val builder = SimulationStateChangeBuilder(state, deviceNetworks)
        val ctx = NetworkContext(0L, observerNetwork, builder, observerId)
        assertNull(ctx.channelsOf(foreignId), "cross-network channel access must return null")
        assertNotNull(ctx.channelsOf(observerId), "same-network channel access must succeed")
    }
}
