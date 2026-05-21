package com.niikelion.ic10_language.logic.state

import com.niikelion.ic10_language.logic.Network
import com.niikelion.ic10_language.logic.devices.DeviceState
import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder

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
    }
}

/**
 * [deviceNetworks] maps deviceId → (networkId, Network) for reachability checks and channel access.
 * Built by Context from its topology before each tick.
 */
class SimulationStateChangeBuilder(
    private val previousState: SimulationState,
    private val deviceNetworks: Map<Long, Pair<Long, Network>> = emptyMap()
) {
    private val devices = mutableMapOf<Long, DeviceStateChangeBuilder>()
    private val networks = mutableMapOf<Long, NetworkStateChangeBuilder>()

    fun device(deviceId: Long): DeviceStateChangeBuilder {
        val deviceState = previousState.devices[deviceId] ?: throw Exception("Device $deviceId not found")
        return devices.getOrPut(deviceId) { DeviceStateChangeBuilder(deviceState) }
    }

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

class SnapshotStateChange(
    private val before: SimulationState,
    private val after: SimulationState
) : IChange<SimulationState> {
    override fun perform(previousState: SimulationState) = after
    override fun revert(nextState: SimulationState) = before
}

fun SimulationState.change(
    deviceNetworks: Map<Long, Pair<Long, Network>> = emptyMap(),
    build: SimulationStateChangeBuilder.() -> Unit
): SimulationState.StateChange {
    val built = SimulationStateChangeBuilder(this, deviceNetworks).apply(build)
    return built.stateChange
}
