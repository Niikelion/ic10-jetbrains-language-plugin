package com.niikelion.ic10_language.logic

import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder
import com.niikelion.ic10_language.logic.state.NetworkStateChangeBuilder
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder

class NetworkContext(
    val id: Long,
    private val topology: Network,
    private val global: SimulationStateChangeBuilder,
    private val observerId: Long
) {
    val reachableIds: Set<Long> get() = topology.networkDevicesVisibleTo(observerId)

    fun canAccess(targetId: Long): Boolean = topology.canAccess(observerId, targetId)

    fun canReadProperty(targetId: Long, propId: Int): Boolean =
        canAccess(targetId) && (global.propertyDefinition(targetId, propId)?.enableRead ?: false)

    fun canWriteProperty(targetId: Long, propId: Int): Boolean =
        canAccess(targetId) && (global.propertyDefinition(targetId, propId)?.enableWrite ?: false)

    fun canReadSlot(targetId: Long, slotIndex: Int, propId: Int): Boolean =
        canAccess(targetId) && (global.slotDefinition(targetId, slotIndex, propId)?.enableRead ?: false)

    fun canWriteSlot(targetId: Long, slotIndex: Int, propId: Int): Boolean =
        canAccess(targetId) && (global.slotDefinition(targetId, slotIndex, propId)?.enableWrite ?: false)

    fun device(targetId: Long): DeviceStateChangeBuilder {
        if (!canAccess(targetId)) throw DeviceNotAccessibleError(targetId)
        return global.device(targetId)
    }

    private fun idsByType(typeHash: Long): List<Long> =
        reachableIds.filter { id -> prefabHashPropId?.let { pid -> global.device(id).property(pid).toLong() == typeHash } ?: false }

    private fun idsByTypeAndName(typeHash: Long, nameHash: Long): List<Long> =
        reachableIds.filter { id ->
            val d = global.device(id)
            (prefabHashPropId?.let { pid -> d.property(pid).toLong() == typeHash } ?: false) &&
            (nameHashPropId?.let { pid -> d.property(pid).toLong() == nameHash } ?: false)
        }

    fun devicesByType(typeHash: Long): List<DeviceStateChangeBuilder> =
        idsByType(typeHash).map { id -> global.device(id) }

    fun devicesByTypeAndName(typeHash: Long, nameHash: Long): List<DeviceStateChangeBuilder> =
        idsByTypeAndName(typeHash, nameHash).map { id -> global.device(id) }

    /** Reads a slot property through the slot's access map; 0 when the slot does not expose it for reading. */
    fun readSlot(targetId: Long, slotIndex: Int, propId: Int): Double {
        val device = device(targetId)
        if (global.slotDefinition(targetId, slotIndex, propId)?.enableRead != true) return 0.0
        return device.slotProperty(slotIndex, propId)
    }

    /** Writes a slot property through the slot's access map; faults when the slot does not expose it for writing. */
    fun writeSlot(targetId: Long, slotIndex: Int, propId: Int, value: Double) {
        val device = device(targetId)
        if (global.slotDefinition(targetId, slotIndex, propId)?.enableWrite != true)
            throw SlotPropertyNotWritableError(slotIndex, propId)
        device.setSlotProperty(slotIndex, propId, value)
    }

    /** Batch slot read by type — only devices that expose the property for reading contribute. */
    fun readSlotsByType(typeHash: Long, slotIndex: Int, propId: Int): List<Double> =
        idsByType(typeHash)
            .filter { id -> canReadSlot(id, slotIndex, propId) }
            .map { id -> global.device(id).slotProperty(slotIndex, propId) }

    /** Batch slot read by type and name — only devices that expose the property for reading contribute. */
    fun readSlotsByTypeAndName(typeHash: Long, nameHash: Long, slotIndex: Int, propId: Int): List<Double> =
        idsByTypeAndName(typeHash, nameHash)
            .filter { id -> canReadSlot(id, slotIndex, propId) }
            .map { id -> global.device(id).slotProperty(slotIndex, propId) }

    /** Batch slot write by type — only devices that expose the property for writing are written. */
    fun writeSlotsByType(typeHash: Long, slotIndex: Int, propId: Int, value: Double) =
        idsByType(typeHash)
            .filter { id -> canWriteSlot(id, slotIndex, propId) }
            .forEach { id -> global.device(id).setSlotProperty(slotIndex, propId, value) }

    fun readChannel(index: Int): Double = global.network(id).readChannel(index)
    fun writeChannel(index: Int, value: Double) = global.network(id).writeChannel(index, value)

    fun channelsOf(deviceId: Long): NetworkStateChangeBuilder? {
        val (netId, _) = global.networkFor(deviceId) ?: return null
        // Channel access is only valid when the target is on the same network as the observer.
        // softConnected devices share network channels but cannot be addressed by property slot.
        if (netId != id) return null
        return global.network(netId)
    }

    companion object {
        private val prefabHashPropId: Int? by lazy { Device.properties["PrefabHash"] }
        private val nameHashPropId: Int? by lazy { Device.properties["NameHash"] }
    }
}
