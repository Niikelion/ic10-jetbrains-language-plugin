package com.niikelion.ic10_language.logic.devices

import com.niikelion.ic10_language.logic.*
import com.niikelion.ic10_language.logic.aspects.Ic10DeviceMemoryAspect
import com.niikelion.ic10_language.logic.aspects.Ic10MemoryAspect
import com.niikelion.ic10_language.logic.aspects.Ic10ProgramAspect
import com.niikelion.ic10_language.logic.state.SimulationState

class StructureCircuitHousing(id: Long, code: ProgramCode): Device(
    id,
    StructureCircuitHousing::class,
    DeviceAspectsBuilder.build {
        define(Ic10ProgramAspect(code, lineNumberId, errorId, ownDeviceId = id))
        implement(Ic10MemoryAspect::class, Ic10DeviceMemoryAspect(STACK_SIZE))
    }
) {
    companion object {
        const val STACK_SIZE = 512

        private val lineNumberId = properties["LineNumber"]!!
        private val errorId = properties["Error"]!!
        private val stackSizeId = properties["StackSize"]!!
    }

    val code = aspect<Ic10ProgramAspect>()!!

    override fun tick(
        current: SimulationState,
        deviceNetworks: Map<Long, Pair<Long, Network>>
    ): Sequence<SimulationState.StateChange> = code.step(id, current, deviceNetworks)

    override fun initialize(): DeviceState {
        val source = super.initialize()
        return DeviceState(
            source.properties + Pair(stackSizeId, STACK_SIZE.toDouble()),
            source.aspects
        )
    }
}