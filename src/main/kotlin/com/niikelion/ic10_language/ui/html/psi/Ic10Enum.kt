package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.psi.Ic10Enum
import com.niikelion.ic10_language.ui.html.Content

val Ic10Enum.html: Content get() = {
    span {
        global(enumName.text)
        operator(".")
        text(enumProperty.text)
    }
}