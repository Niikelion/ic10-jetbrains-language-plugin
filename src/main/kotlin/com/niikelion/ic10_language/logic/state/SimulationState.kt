package com.niikelion.ic10_language.logic.state

import com.niikelion.ic10_language.logic.devices.DeviceState
import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder

class SimulationState(
    val devices: Map<Long, DeviceState> = emptyMap()
) {
    class StateChange(
        val devices: Map<Long, DeviceState.StateChange> = emptyMap()
    ) : CompositeChange<SimulationState> {
        override fun compose(source: SimulationState, action: CompositeChangeAction) = action.compose {
            SimulationState(compose(source.devices, devices))
        }
    }
}

class SimulationStateChangeBuilder(
    private val previousState: SimulationState
) {
    private val devices = mutableMapOf<Long, DeviceStateChangeBuilder>()

    fun device(deviceId: Long): DeviceStateChangeBuilder {
        val deviceState = previousState.devices[deviceId] ?: throw Exception("Device $deviceId not found")
        return devices.getOrPut(deviceId) { DeviceStateChangeBuilder(deviceState) }
    }
    val stateChange get() = SimulationState.StateChange(devices.mapValues { it.value.stateChange })
}

fun SimulationState.change(build: SimulationStateChangeBuilder.() -> Unit): SimulationState.StateChange {
    val built = SimulationStateChangeBuilder(this).apply(build)
    return built.stateChange
}