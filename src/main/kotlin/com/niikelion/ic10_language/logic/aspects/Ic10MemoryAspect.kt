package com.niikelion.ic10_language.logic.aspects

import com.niikelion.ic10_language.logic.devices.DeviceStateChangeBuilder

interface Ic10MemoryAspect: DeviceAspect {
    val size: Int

    override fun initialize(): DeviceAspect.State

    interface State: DeviceAspect.State {
        override fun change(): DeviceAspect.State.Change.Builder

        interface Change: DeviceAspect.State.Change {
            interface Builder: DeviceAspect.State.Change.Builder {
                val size: Int
                fun read(address: Int): Double
                fun write(address: Int, value: Double)
            }
        }
    }
}

fun Ic10MemoryAspect.State.Change.Builder.clear() {
    for (i in 0 until size) write(i, 0.0)
}

val DeviceStateChangeBuilder.memory get() = aspect<Ic10MemoryAspect.State, Ic10MemoryAspect.State.Change.Builder>()