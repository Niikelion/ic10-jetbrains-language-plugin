package com.niikelion.ic10_language.test.logic

import com.niikelion.ic10_language.logic.DeviceSlots
import com.niikelion.ic10_language.logic.Registers
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceValidationTests {
    @Test
    fun directRegisterNamesAreValid() {
        assertTrue(Registers.isValidName("r0"))
        assertTrue(Registers.isValidName("r17"))
        assertTrue(Registers.isValidName("ra"))
        assertTrue(Registers.isValidName("sp"))
    }

    @Test
    fun indirectRegisterReferencesAreValid() {
        assertTrue(Registers.isValidReference("r0"))
        assertTrue(Registers.isValidReference("rr0"))
        assertTrue(Registers.isValidReference("rrr0"))
        assertTrue(Registers.isValidReference("rr17"))
    }

    @Test
    fun invalidRegisterReferencesAreRejected() {
        assertFalse(Registers.isValidReference("r18"))
        assertFalse(Registers.isValidReference("rra"))
        assertFalse(Registers.isValidReference("foo"))
    }

    @Test
    fun directDeviceNamesAreValid() {
        assertTrue(DeviceSlots.isValidName("db"))
        assertTrue(DeviceSlots.isValidName("d0"))
        assertTrue(DeviceSlots.isValidName("d5"))
    }

    // Regression: indirect device references (drX) were flagged as errors because
    // isValidReference passed the full string to Registers.isValidReference instead
    // of the extracted register source.
    @Test
    fun indirectDeviceReferencesAreValid() {
        assertTrue(DeviceSlots.isValidReference("dr0"))
        assertTrue(DeviceSlots.isValidReference("dr5"))
        assertTrue(DeviceSlots.isValidReference("drr0"))
        assertTrue(DeviceSlots.isValidReference("drrr5"))
    }

    @Test
    fun invalidDeviceReferencesAreRejected() {
        assertFalse(DeviceSlots.isValidReference("d0"))
        assertFalse(DeviceSlots.isValidReference("dr18"))
        assertFalse(DeviceSlots.isValidReference("dra"))
        assertFalse(DeviceSlots.isValidReference("foo"))
    }
}
