package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.psi.Ic10Number
import com.niikelion.ic10_language.ui.html.Content

val Ic10Number.html: Content get() = {
    number(text)
}