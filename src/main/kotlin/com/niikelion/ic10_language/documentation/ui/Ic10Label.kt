package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.ui.html.psi.html
import com.niikelion.ic10_language.utils.render

val Ic10Label.doc: Content get() = {
    definition {
        pre {
            separated(" ") {
                keyword("label")
                render(this@doc.html)
            }
        }
    }
    sectionsTable {
        sectionContent {
            separated(" ") {
                text("line")
                number(getLineNumber().toString())
            }
        }
    }
}