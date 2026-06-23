package com.niikelion.ic10_language.logic.state

import com.niikelion.ic10_language.logic.DeviceNotFoundError
import com.niikelion.ic10_language.logic.Network
import com.niikelion.ic10_language.logic.devices.DeviceInfo
import com.niikelion.ic10_language.logic.devices.DeviceState
import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder
import com.niikelion.ic10_language.logic.devices.PropertyDefinition

class SimulationState(
    val devices: Map<Long, DeviceState> = emptyMap(),
    val networks: Map<Long, NetworkState> = emptyMap()
) {
    class StateChange(
        val devices: Map<Long, DeviceState.StateChange> = emptyMap(),
        val networks: Map<Long, NetworkStateChange> = emptyMap()
    ) : CompositeChange<SimulationState> {
        override fun compose(source: SimulationState, action: CompositeChangeAction) = action.compose {
            SimulationState(
                compose(source.devices, devices),
                compose(source.networks, networks)
            )
        }

        operator fun plus(other: StateChange): StateChange {
            val mergedDevices = buildMap {
                for (key in devices.keys + other.devices.keys) {
                    val a = devices[key]
                    val b = other.devices[key]
                    put(key, when {
                        a == null -> b!!
                        b == null -> a
                        else -> a + b
                    })
                }
            }
            val mergedNetworks = buildMap {
                for (key in networks.keys + other.networks.keys) {
                    val a = networks[key]
                    val b = other.networks[key]
                    put(key, when {
                        a == null -> b!!
                        b == null -> a
                        else -> a + b
                    })
                }
            }
            return StateChange(mergedDevices, mergedNetworks)
        }
    }
}

/**
 * [deviceNetworks] maps deviceId → (networkId, Network) for reachability checks and channel access.
 * Built by Context from its topology before each tick.
 */
class SimulationStateChangeBuilder(
    private val previousState: SimulationState,
    private val deviceNetworks: Map<Long, Pair<Long, Network>> = emptyMap(),
    private val deviceDefinitions: Map<Long, DeviceInfo> = emptyMap()
) {
    private val devices = mutableMapOf<Long, DeviceStateChangeBuilder>()
    private val networks = mutableMapOf<Long, NetworkStateChangeBuilder>()

    fun device(deviceId: Long): DeviceStateChangeBuilder {
        val deviceState = previousState.devices[deviceId] ?: throw DeviceNotFoundError(deviceId)
        return devices.getOrPut(deviceId) { DeviceStateChangeBuilder(deviceState) }
    }

    fun propertyDefinition(deviceId: Long, propId: Int): PropertyDefinition? =
        deviceDefinitions[deviceId]?.properties?.get(propId)

    fun prefabHash(deviceId: Long): Long? = deviceDefinitions[deviceId]?.prefabHash

    fun network(networkId: Long): NetworkStateChangeBuilder {
        val networkState = previousState.networks[networkId] ?: NetworkState()
        return networks.getOrPut(networkId) { NetworkStateChangeBuilder(networkState) }
    }

    /** Returns (networkId, Network) for the given device, or null if it is not on any network. */
    fun networkFor(deviceId: Long): Pair<Long, Network>? = deviceNetworks[deviceId]

    val stateChange get() = SimulationState.StateChange(
        devices.mapValues { it.value.stateChange },
        networks.mapValues { it.value.result }
    )
}

fun SimulationState.change(
    deviceNetworks: Map<Long, Pair<Long, Network>> = emptyMap(),
    deviceDefinitions: Map<Long, DeviceInfo> = emptyMap(),
    build: SimulationStateChangeBuilder.() -> Unit
): SimulationState.StateChange {
    val built = SimulationStateChangeBuilder(this, deviceNetworks, deviceDefinitions).apply(build)
    return built.stateChange
}
