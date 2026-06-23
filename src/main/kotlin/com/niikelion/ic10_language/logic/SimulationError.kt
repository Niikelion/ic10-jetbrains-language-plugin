package com.niikelion.ic10_language.logic

/**
 * Base type for every error condition raised while executing a simulation.
 *
 * Using a sealed hierarchy instead of generic [Exception] lets callers (most notably the
 * debugger) distinguish error kinds and surface meaningful reasons to the user.
 */
sealed class SimulationError(message: String) : Exception(message)

/** A device with the given [id] does not exist in the simulation. */
class DeviceNotFoundError(val id: Long) : SimulationError("Device $id not found")

/** The observing device is not allowed to access the device with the given [id]. */
class DeviceNotAccessibleError(val id: Long) : SimulationError("Device $id not accessible")

/** The device with the given [id] is not connected to any network. */
class DeviceNotOnNetworkError(val id: Long) : SimulationError("Device $id not on any network")

/** Whether a memory access that failed was a read or a write. */
enum class MemoryAccess { READ, WRITE }

/** A memory [access] targeted [address], which is outside the valid range. */
class MemoryBoundsError(val access: MemoryAccess, val address: Int) :
    SimulationError("Memory ${if (access == MemoryAccess.READ) "read" else "write"} at address $address is out of bounds")

/** Register reference indirection of [depth] exceeded the supported [max] depth. */
class IndirectionDepthError(val depth: Int, val max: Int) :
    SimulationError("Register reference indirection depth $depth exceeds maximum of $max")

/** An operand was not of the [expected] kind (e.g. register, device slot, value). */
class OperandTypeError(val expected: String) : SimulationError("Expected $expected")

/** An unresolved [value] could not be resolved against the current program state. */
class ValueResolutionError(val value: IUnresolvedValue) : SimulationError("Error resolving $value")

/** Tried to access an aspect that the device does not have. */
class AspectNotFoundError : SimulationError("Cannot access device aspect that does not exist")

/** An aspect's state change builder was not of the expected type. */
class AspectTypeMismatchError : SimulationError("Aspect state change builder type mismatch")

/** Tried to access a property that the device does not have. */
class PropertyNotFoundError : SimulationError("Cannot access device property that does not exist")
