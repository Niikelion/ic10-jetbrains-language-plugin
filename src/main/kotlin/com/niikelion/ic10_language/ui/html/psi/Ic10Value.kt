package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.psi.Ic10Value
import com.niikelion.ic10_language.utils.render

val Ic10Value.html: Content get() = {
    val content = networkRef?.html
        ?: macro?.html
        ?: number?.html
        ?: enum?.html

    render(content ?: { text(text) })
}