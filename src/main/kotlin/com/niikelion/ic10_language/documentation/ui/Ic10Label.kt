package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.psi.Ic10Label
import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.ui.html.psi.html

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
                number(Ic10PsiUtils.getLineNumber(this@doc).toString())
            }
        }
    }
}