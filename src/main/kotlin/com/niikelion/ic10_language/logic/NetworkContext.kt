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

    fun device(targetId: Long): DeviceStateChangeBuilder {
        if (!canAccess(targetId)) throw Exception("Device $targetId not accessible")
        return global.device(targetId)
    }

    fun devicesByType(typeHash: Long): List<DeviceStateChangeBuilder> =
        reachableIds
            .filter { id -> prefabHashPropId?.let { pid -> global.device(id).property(pid).toLong() == typeHash } ?: false }
            .map { id -> global.device(id) }

    fun devicesByTypeAndName(typeHash: Long, nameHash: Long): List<DeviceStateChangeBuilder> =
        reachableIds.filter { id ->
            val d = global.device(id)
            (prefabHashPropId?.let { pid -> d.property(pid).toLong() == typeHash } ?: false) &&
            (nameHashPropId?.let { pid -> d.property(pid).toLong() == nameHash } ?: false)
        }.map { id -> global.device(id) }

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
