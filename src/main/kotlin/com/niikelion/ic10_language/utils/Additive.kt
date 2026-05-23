package com.niikelion.ic10_language.utils

interface Additive<T> {
    operator fun plus(other: T): T
}