package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.psi.Ic10Channel
import com.niikelion.ic10_language.ui.html.Content

val Ic10Channel.html: Content get() = {
    span {
        text(referenceName.text)
        operator(":")
        text(channelNumber.text)
    }
}