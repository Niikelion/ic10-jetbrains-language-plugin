package com.niikelion.ic10_language.logic

import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.state.NetworkState
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.logic.state.change

class Context(
    val devices: List<Device>,
    val networks: Map<Long, Network> = mapOf(0L to Network.single(emptySet()))
) {
    companion object {
        fun from(vararg devices: Device): Context = Context(devices.toList())
    }

    /** deviceId → (networkId, Network), built once from the topology. */
    private val deviceNetworks: Map<Long, Pair<Long, Network>> by lazy {
        networks.flatMap { (networkId, network) ->
            (network.dataConnected + network.softConnected).map { deviceId ->
                deviceId to Pair(networkId, network)
            }
        }.toMap()
    }

    fun step(state: SimulationState): Sequence<SimulationState.StateChange> = sequence {
        var current = state
        devices.forEach { device ->
            for (change in device.tick(current, deviceNetworks)) {
                yield(change)
                current = change.perform(current)
            }
        }
    }

    fun endTick(state: SimulationState): SimulationState.StateChange = state.change(deviceNetworks) {
        devices.forEach { device -> device.tickEnd(this) }
    }

    fun initialize(): SimulationState = SimulationState(
        devices.associateBy { it.id }.mapValues { (_, device) -> device.initialize() },
        networks.mapValues { NetworkState() }
    )
}
