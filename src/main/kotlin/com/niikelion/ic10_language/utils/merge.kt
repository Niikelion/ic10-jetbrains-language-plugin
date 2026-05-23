package com.niikelion.ic10_language.utils

fun <K, V> Map<K, V>.mergeWith(
    other: Map<K, V>,
    onConflict: (K, V, V) -> V
): Map<K, V> {
    val commonKeys = keys intersect other.keys

    return this + other + commonKeys.associateWith {
        onConflict(it, getValue(it), other.getValue(it))
    }
}