package com.niikelion.ic10_language.utils

fun <T> Iterable<T>.splitWhen(pred: (T) -> Boolean): List<List<T>> {
    val res = mutableListOf<MutableList<T>>()
    var cur = mutableListOf<T>()

    for (e in this) {
        if (pred(e)) {
            if (cur.isNotEmpty()) {
                res += cur
                cur = mutableListOf()
            }
        } else {
            cur += e
        }
    }
    if (cur.isNotEmpty()) res += cur
    return res
}
fun <T> Array<T>.splitWhen(pred: (T) -> Boolean): List<List<T>> = asIterable().splitWhen(pred)