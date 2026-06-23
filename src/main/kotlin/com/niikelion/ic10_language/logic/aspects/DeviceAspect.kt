package com.niikelion.ic10_language.logic.aspects

import com.niikelion.ic10_language.logic.state.IChange
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder
import kotlin.reflect.KClass

interface DeviceAspect {
    val name: String
    val debuggerLabel: String get() = this::class.simpleName ?: name
    val debuggerType: String? get() = null
    val debuggerTableView: Boolean get() = false
    val stateClass: KClass<out State>

    fun tick(state: SimulationStateChangeBuilder, deviceId: Long) {}
    fun tickEnd(state: SimulationStateChangeBuilder, deviceId: Long) {}
    fun initialize(): State

    interface State {
        fun change(): Change.Builder
        fun debuggerEntries(): List<Pair<String, Double>> = emptyList()

        /**
         * A human-readable status line surfaced at the top of the device node in the debugger,
         * or `null` when the aspect has nothing to report. Used to explain a halted device —
         * e.g. an `hcf` self-destruct versus a runtime error and its reason.
         */
        fun debuggerStatus(): String? = null

        interface Change: IChange<State> {
            /** Compose two changes for the same aspect: keeps this change's previousValue and [other]'s nextValue. */
            operator fun plus(other: Change): Change

            interface Builder {
                val result: Change
            }
        }
    }
}