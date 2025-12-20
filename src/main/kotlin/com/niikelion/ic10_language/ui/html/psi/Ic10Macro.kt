package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.psi.Ic10Macro
import com.niikelion.ic10_language.ui.html.Content

val Ic10Macro.html: Content get() = {
    span {
        text(macroName.text)
        string(macroValue.text)
        text(")")
    }
}