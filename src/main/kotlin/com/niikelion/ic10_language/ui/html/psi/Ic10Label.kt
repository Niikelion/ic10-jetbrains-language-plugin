package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.ui.html.Content

val Ic10Label.html: Content get() = {
    text(labelName.text)
}