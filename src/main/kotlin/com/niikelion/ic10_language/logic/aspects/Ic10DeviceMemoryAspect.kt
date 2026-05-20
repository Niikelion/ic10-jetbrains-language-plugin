package com.niikelion.ic10_language.logic.aspects

import com.intellij.openapi.Disposable
import com.intellij.ui.components.JBTextField
import com.niikelion.ic10_language.logic.state.CompositeChange
import com.niikelion.ic10_language.logic.state.CompositeChangeAction
import com.niikelion.ic10_language.logic.state.SimpleChange
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import com.niikelion.ic10_language.ui.swing.fillWidth
import com.niikelion.ic10_language.ui.swing.jb.label
import com.niikelion.ic10_language.ui.swing.layout.Alignment
import com.niikelion.ic10_language.ui.swing.layout.Justification
import com.niikelion.ic10_language.ui.swing.layout.row
import com.niikelion.ic10_language.ui.swing.layout.wrap
import com.niikelion.ic10_language.ui.swing.maxWidth
import com.niikelion.ic10_language.ui.swing.state.coroutineScope
import com.niikelion.ic10_language.utils.toPrettyString
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.Dimension
import javax.swing.JComponent
import kotlin.reflect.KClass

class Ic10DeviceMemoryAspect(
    override val size: Int
): Ic10MemoryAspect {
    override val name = "Device Memory"
    override val stateClass: KClass<out DeviceAspect.State> = State::class

    override fun renderDebuggerView(
        flow: StateFlow<DeviceAspect.State>,
        scope: Disposable
    ): JComponent = SwingBuilder().wrap({ gap(4) }) {
        val initialValue = flow.value
        if (initialValue !is State) return@wrap

        initialValue.contents.forEachIndexed { index, value ->
            row({ align(Alignment.CENTER); justify(Justification.FILL) }) {
                label("${index}:")
                element {
                    JBTextField(value.toPrettyString()).also {
                        it.isEditable = false
                        it.isEnabled = false
                        it.maxWidth(100)
                        it.fillWidth()
                        flow.onEach { state ->
                            if (state !is State) return@onEach
                            it.text = state.contents[index].toPrettyString()
                        }.launchIn(scope.coroutineScope())
                    }
                }
            }.also {
                it.maxWidth(160)
                it.preferredSize = Dimension(160, it.preferredSize.height)
            }
        }
    }

    override fun initialize(): State = State(
        contents = Array(size) { 0.0 }
    )

    class State(
        val contents: Array<Double>
    ): Ic10MemoryAspect.State {
        override fun change(): Change.Builder = Change.Builder(this)

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