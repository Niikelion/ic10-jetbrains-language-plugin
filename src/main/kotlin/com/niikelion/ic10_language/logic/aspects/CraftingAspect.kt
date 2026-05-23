package com.niikelion.ic10_language.logic.aspects

import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder

interface CraftingAspect : DeviceAspect {
    interface State : DeviceAspect.State {
        override fun change(): Change.Builder

        interface Change : DeviceAspect.State.Change {
            interface Builder : DeviceAspect.State.Change.Builder {
                fun read(reagentHash: Long): Double
                fun write(reagentHash: Long, quantity: Double)
            }
        }
    }
}

val DeviceStateChangeBuilder.crafting get() = aspectOrNull<CraftingAspect.State, CraftingAspect.State.Change.Builder>()
