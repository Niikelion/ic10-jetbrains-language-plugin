package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.psi.Ic10NetworkRef
import com.niikelion.ic10_language.ui.html.Content

val Ic10NetworkRef.html: Content get() = {
    span {
        text(referenceName.text)
        operator(":")
        text(portIndex.text)
    }
}
