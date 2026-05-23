package com.niikelion.ic10_language.logic.aspects

import com.niikelion.ic10_language.logic.Reagents
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.state.CompositeChange
import com.niikelion.ic10_language.logic.state.CompositeChangeAction
import com.niikelion.ic10_language.logic.state.SimpleChange
import com.niikelion.ic10_language.logic.state.SimulationStateChangeBuilder
import kotlin.math.roundToLong
import kotlin.reflect.KClass

class Ic10CraftingAspect : CraftingAspect {
    override val name = "Reagents"
    override val debuggerType = "Double[]"
    override val debuggerTableView = true
    override val stateClass: KClass<out DeviceAspect.State> = State::class

    override fun initialize(): State = State(emptyMap())

    override fun tickEnd(state: SimulationStateChangeBuilder, deviceId: Long) {
        val recipeHashPropId = Device.properties["RecipeHash"] ?: return
        if (state.propertyDefinition(deviceId, recipeHashPropId) == null) return
        val device = state.device(deviceId)
        val recipeHash = device.property(recipeHashPropId).toLong()
        if (recipeHash == 0L) return
        val devicePrefabHash = state.prefabHash(deviceId) ?: return
        if (Reagents.recipeFor(devicePrefabHash, recipeHash) == null) {
            device.setProperty(recipeHashPropId, 0.0)
        }
    }

    class State(
        val contents: Map<Long, Double>
    ) : CraftingAspect.State {
        override fun change(): Change.Builder = Change.Builder(this)

        override fun debuggerEntries(): List<Pair<String, Double>> =
            contents.map { (hash, quantity) -> (Reagents.nameByHash[hash] ?: hash.toString()) to quantity }

        class Change(
            val contents: Map<Long, SimpleChange<Double>>
        ) : CraftingAspect.State.Change, CompositeChange<DeviceAspect.State> {
            override fun compose(source: DeviceAspect.State, action: CompositeChangeAction): DeviceAspect.State {
                if (source !is State) return source
                return action.compose {
                    State(source.contents + contents.mapValues { it.value.nextValue })
                }
            }

            class Builder(private val previousState: State) : CraftingAspect.State.Change.Builder {
                private val changes = mutableMapOf<Long, SimpleChange<Double>>()

                override fun read(reagentHash: Long): Double =
                    changes[reagentHash]?.nextValue ?: previousState.contents[reagentHash] ?: 0.0

                override fun write(reagentHash: Long, quantity: Double) {
                    val rounded = (quantity * 10).roundToLong() / 10.0
                    changes[reagentHash] = SimpleChange(previousState.contents[reagentHash] ?: 0.0, rounded)
                }

                override val result get() = Change(changes)
            }
        }
    }
}
