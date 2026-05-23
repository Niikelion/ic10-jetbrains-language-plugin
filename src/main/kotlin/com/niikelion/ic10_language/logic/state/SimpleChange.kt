package com.niikelion.ic10_language.logic.state

import com.niikelion.ic10_language.utils.Additive

interface IChange<V> {
    fun perform(previousState: V): V
    fun revert(nextState: V): V
}

fun <K, T, V: IChange<T>>Map<K, V>.perform(previousState: Map<K, T>) =
    previousState.mapValues { entry -> get(entry.key)?.perform(entry.value) ?: entry.value }
fun <K, T, V: IChange<T>>Map<K, V>.revert(nextState: Map<K, T>) =
    nextState.mapValues { entry -> get(entry.key)?.revert(entry.value) ?: entry.value }

class SimpleChange<V>(
    val previousValue: V,
    val nextValue: V
): IChange<V>, Additive<SimpleChange<V>> {
    override fun perform(previousState: V) = nextValue
    override fun revert(nextState: V) = previousValue

    override operator fun plus(other: SimpleChange<V>): SimpleChange<V> = SimpleChange(previousValue, other.nextValue)
}

class CompositeChangeAction(val type: Type) {
    enum class Type {
        PERFORM, REVERT
    }

    companion object {
        val perform = CompositeChangeAction(Type.PERFORM)
        val revert = CompositeChangeAction(Type.REVERT)
    }

    fun <T, V: IChange<T>> compose(source: T, change: V?): T =
        if (change == null) source else when (type) {
            Type.PERFORM -> change.perform(source)
            Type.REVERT -> change.revert(source)
        }

    fun <T> compose(builder: CompositeChangeAction.() -> T): T = builder()
}

interface CompositeChange<V>: IChange<V> {

    fun compose(source: V, action: CompositeChangeAction): V

    override fun perform(previousState: V) = compose(previousState, CompositeChangeAction.perform)
    override fun revert(nextState: V) = compose(nextState, CompositeChangeAction.revert)
}

fun <K, T, V: IChange<T>>CompositeChangeAction.compose(source: Map<K, T>, change: Map<K, V>): Map<K, T> = when (type) {
    CompositeChangeAction.Type.PERFORM -> change.perform(source)
    CompositeChangeAction.Type.REVERT -> change.revert(source)
}