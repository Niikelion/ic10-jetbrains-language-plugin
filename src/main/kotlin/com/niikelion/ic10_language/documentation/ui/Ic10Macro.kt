package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.logic.value
import com.niikelion.ic10_language.psi.Ic10Macro
import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.ui.html.psi.html

val Ic10Macro.doc: Content get() = {
    definition {
        pre {
            render(this@doc.html)
        }
    }
    this@doc.value?.let { "$it" }?.apply {
        sectionsTable {
            sectionContent {
                number(this@apply)
            }
        }
    }
}