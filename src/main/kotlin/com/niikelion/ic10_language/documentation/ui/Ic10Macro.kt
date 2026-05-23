package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.logic.intValue
import com.niikelion.ic10_language.psi.Ic10Macro
import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.ui.html.psi.html
import com.niikelion.ic10_language.utils.render

val Ic10Macro.doc: Content get() = {
    definition {
        pre {
            render(this@doc.html)
        }
    }
    this@doc.intValue?.let { "$it" }?.apply {
        sectionsTable {
            sectionContent {
                number(this@apply)
            }
        }
    }
}