package com.niikelion.ic10_language.logic.devices

import com.niikelion.ic10_language.logic.DeviceDataRegistry
import com.niikelion.ic10_language.logic.Network
import com.niikelion.ic10_language.logic.aspects.CraftingAspect
import com.niikelion.ic10_language.logic.aspects.Ic10CraftingAspect
import com.niikelion.ic10_language.logic.state.SimulationState

/**
 * Shared implementation for all fabricator-style devices (Autolathe, PipeBender, etc.).
 * The only thing that varies between crafters is their recipe list, which comes from
 * the stationpedia via [DeviceDataRegistry].
 */
class StructureCrafter(
    id: Long,
    data: DeviceDataRegistry.Entry,
    customName: String? = null
) : Device(
    id,
    data,
    DeviceAspectsBuilder.build {
        implement(CraftingAspect::class, Ic10CraftingAspect())
    },
    customName
) {
    override fun tick(
        current: SimulationState,
        deviceNetworks: Map<Long, Pair<Long, Network>>,
        deviceDefinitions: Map<Long, DeviceInfo>
    ): Sequence<SimulationState.StateChange> = sequence {
        // Crafters are passive during a tick; their logic runs in tickEnd via Ic10CraftingAspect.
    }
}
