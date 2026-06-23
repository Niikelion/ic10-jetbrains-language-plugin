package com.niikelion.ic10_language.test.logic

import com.niikelion.ic10_language.logic.IProgramState
import com.niikelion.ic10_language.logic.Register
import com.niikelion.ic10_language.logic.RegisterReferenceValue
import com.niikelion.ic10_language.logic.Registers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class RegisterReferenceDepthTests {
    // Every register holds 0, so each indirection step points back to r0.
    private val state = object : IProgramState {
        override fun get(register: Register) = 0.0
    }

    @Test
    fun resolvesAtMaximumDepth() {
        val ref = RegisterReferenceValue(Registers.get(0)!!, RegisterReferenceValue.MAX_INDIRECTION)
        assertEquals(Registers.get(0), ref.resolve(state)?.value)
    }

    @Test
    fun throwsBeyondMaximumDepth() {
        val ref = RegisterReferenceValue(Registers.get(0)!!, RegisterReferenceValue.MAX_INDIRECTION + 1)
        try {
            ref.resolve(state)
            fail("Expected an exception for indirection depth beyond the maximum")
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("indirection"))
        }
    }

    @Test
    fun deeplyNestedReferenceFromStringThrows() {
        // 12 leading 'r' characters => 11 levels of indirection, one past the cap.
        val ref = RegisterReferenceValue.fromString("rrrrrrrrrrrr0")!!
        assertEquals(RegisterReferenceValue.MAX_INDIRECTION + 1, ref.hoops)
        try {
            ref.resolve(state)
            fail("Expected an exception for indirection depth beyond the maximum")
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("indirection"))
        }
    }
}
