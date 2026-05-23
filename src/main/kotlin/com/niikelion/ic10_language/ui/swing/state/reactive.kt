package com.niikelion.ic10_language.ui.swing.state

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.niikelion.ic10_language.ui.swing.SwingBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.BorderLayout
import javax.swing.JComponent

fun <T>SwingBuilder.reactive(parentDisposable: Disposable, flow: StateFlow<T>, content: SwingBuilder.(value: T, scope: Disposable) -> JComponent): JComponent {
    var previousValue = flow.value

    val wrapper = SwappableComponent().also(this::insert)
    Disposer.register(parentDisposable, wrapper)

    fun regenerateState(value: T) {
        wrapper.makeComponent { scope ->
            flow.onEach { value ->
                if (previousValue == value) return@onEach
                previousValue = value
                regenerateState(value)
            }.launchIn(scope.coroutineScope())

            SwingBuilder().content(value, scope)
        }
    }

    regenerateState(previousValue)
    return wrapper
}

class SwappableComponent(initial: JComponent? = null): JComponent(), Disposable {
    private var current: JComponent? = null
    private var currentDisposable: Disposable? = null

    var component: JComponent?
        get() = current
        set(value) {
            current?.let { old ->
                remove(old)
                if (old is Disposable) {
                    Disposer.dispose(old)
                }
            }
            currentDisposable?.also {
                Disposer.dispose(it)
                currentDisposable = null
            }
            current = value

            value?.let { comp ->
                add(comp, BorderLayout.CENTER)
                if (comp is Disposable) {
                    Disposer.register(this, comp)
                }
            }

            revalidate()
            repaint()
        }

    fun makeComponent(builder: (disposable: Disposable) -> JComponent) {
        component = null
        val disposable = Disposer.newDisposable(this, "Reactive scope")
        component = builder(disposable)
        currentDisposable = disposable
    }

    init {
        layout = BorderLayout()
        component = initial
    }

    override fun dispose() {
        component = null
    }
}

fun Disposable.coroutineScope(): CoroutineScope {
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + job)

    Disposer.register(this) {
        job.cancel()
    }

    return scope
}