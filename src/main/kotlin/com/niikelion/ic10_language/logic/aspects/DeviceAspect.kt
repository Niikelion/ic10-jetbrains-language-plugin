package com.niikelion.ic10_language.logic.aspects

import com.intellij.openapi.Disposable
import com.niikelion.ic10_language.logic.state.IChange
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder
import kotlinx.coroutines.flow.StateFlow
import javax.swing.JComponent
import kotlin.reflect.KClass

interface DeviceAspect {
    val name: String
    val stateClass: KClass<out State>

    fun renderDebuggerView(flow: StateFlow<State>, scope: Disposable): JComponent?
    fun tick(state: SimulationStateChangeBuilder, deviceId: Long) {}
    fun tickEnd(state: SimulationStateChangeBuilder, deviceId: Long) {}
    fun initialize(): State

    interface State {
        fun change(): Change.Builder

        interface Change: IChange<State> {
            interface Builder {
                val result: Change
            }
        }
    }
}