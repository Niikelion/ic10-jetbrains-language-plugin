package com.niikelion.ic10_language.test.logic

import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DebuggerStatusTests {
    @Test
    fun cleanStateHasNoStatus() {
        assertNull(Ic10ProgramAspect.State().status)
    }

    @Test
    fun runtimeErrorStatusReportsReason() {
        val status = Ic10ProgramAspect.State(icError = "Device 5 not found").status
        assertNotNull(status)
        assertTrue("status should mention it is an error", status!!.contains("error", ignoreCase = true))
        assertTrue("status should include the reason", status.contains("Device 5 not found"))
    }

    @Test
    fun hcfStatusIsDistinctFromRuntimeError() {
        val onFire = Ic10ProgramAspect.State(onFire = true).status
        val error = Ic10ProgramAspect.State(icError = "boom").status
        assertNotNull(onFire)
        assertNotNull(error)
        assertTrue("hcf status should mention fire", onFire!!.contains("fire", ignoreCase = true))
        assertNotEquals(onFire, error)
    }

    // hcf and runtime errors are tracked independently in the state model.
    @Test
    fun fireAndErrorAreSeparateFields() {
        val onFire = Ic10ProgramAspect.State(onFire = true)
        assertTrue(onFire.onFire)
        assertNull(onFire.icError)

        val errored = Ic10ProgramAspect.State(icError = "boom")
        assertFalse(errored.onFire)
        assertEquals("boom", errored.icError)
    }
}
