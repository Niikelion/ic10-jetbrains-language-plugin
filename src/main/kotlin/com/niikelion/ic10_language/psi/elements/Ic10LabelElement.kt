package com.niikelion.ic10_language.psi.elements

interface Ic10LabelElement: Ic10NamedElement, Ic10ValueLikeElement, Ic10EntitySourceElement {
    fun getLineNumber(): Int
}