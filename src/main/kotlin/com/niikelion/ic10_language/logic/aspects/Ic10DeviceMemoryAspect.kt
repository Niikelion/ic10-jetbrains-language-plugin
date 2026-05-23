package com.niikelion.ic10_language.logic.aspects

import com.niikelion.ic10_language.logic.state.CompositeChange
import com.niikelion.ic10_language.logic.state.CompositeChangeAction
import com.niikelion.ic10_language.logic.state.SimpleChange
import com.niikelion.ic10_language.utils.toPrettyString
import kotlin.reflect.KClass

class Ic10DeviceMemoryAspect(
    override val size: Int
): Ic10MemoryAspect {
    override val name = "Device Memory"
    override val debuggerLabel = "{size = $size}"
    override val debuggerType = "Double[]"
    override val debuggerTableView = true
    override val stateClass: KClass<out DeviceAspect.State> = Ic10MemoryAspect.State::class

    override fun initialize(): State = State(
        contents = Array(size) { 0.0 }
    )

    class State(
        val contents: Array<Double>
    ): Ic10MemoryAspect.State {
        override fun change(): Change.Builder = Change.Builder(this)

        override fun debuggerEntries(): List<Pair<String, Double>> = buildList {
            add("size" to contents.size.toDouble())
            contents.forEachIndexed { index, value -> add("[$index]" to value) }
        }

        class Change(
            val contents: Map<Int, SimpleChange<Double>>
        ): Ic10MemoryAspect.State.Change, CompositeChange<DeviceAspect.State> {
            override fun compose(source: DeviceAspect.State, action: CompositeChangeAction): DeviceAspect.State {
                if (source !is State) return source

                return action.compose {
                    State(
                        source.contents.mapIndexed { index, value ->
                            compose(value, contents[index])
                        }.toTypedArray()
                    )
                }
            }

            class Builder(val previousState: State): Ic10MemoryAspect.State.Change.Builder {
                private val contents = mutableMapOf<Int, SimpleChange<Double>>()

                override val size get() = previousState.contents.size

                override fun read(address: Int): Double =
                    contents[address]?.nextValue
                        ?: previousState.contents.elementAtOrNull(address)
                        ?: throw Exception("Read outside stack bounds")

                override fun write(address: Int, value: Double) {
                    if (address < 0 || address >= previousState.contents.size)
                        throw Exception("Write outside stack bounds")
                    contents[address] = SimpleChange(previousState.contents[address], value)
                }

                override val result get() = Change(contents)
            }
        }
    }
}