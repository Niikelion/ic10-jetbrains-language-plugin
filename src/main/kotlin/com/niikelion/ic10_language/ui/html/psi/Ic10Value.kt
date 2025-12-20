package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.psi.Ic10Value

val Ic10Value.html: Content get() = {
    val content = channel?.html
        ?: macro?.html
        ?: number?.html
        ?: constant?.html

    render(content ?: { text(text) })
}