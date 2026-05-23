package com.niikelion.ic10_language.utils

fun Double.toPrettyString(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()