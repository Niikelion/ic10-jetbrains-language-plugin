package com.niikelion.ic10_language.utils

open class StructuralDSL<T> {
    protected val elements = mutableListOf<T>()

    fun insert(element: T) {
        elements.add(element)
    }
}

fun <D: StructuralDSL<*>> D.render(content: D.() -> Unit) = content()