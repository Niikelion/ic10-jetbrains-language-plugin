package com.niikelion.ic10_language.logic

import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.logic.state.change

class Context(
    val devices: List<Device>,
    val network: Network = Network.single(devices.map { it.id })
) {
    companion object {
        fun from(vararg devices: Device): Context {
            return Context(devices.toList())
        }
    }

    fun tick(state: SimulationState): SimulationState.StateChange = state.change {
        devices.forEach { device ->
            device.tick(this)
        }
    }

    fun initialize(): SimulationState = SimulationState(
        devices.associateBy { it.id }.mapValues { (_, device) -> device.initialize() }
    )
}