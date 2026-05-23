package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.logic.Enum
import com.niikelion.ic10_language.ui.html.Content

fun Enum.doc(): Content = {
    definition {
        pre {
            separated(" ") {
                keyword("enum")
                text(name)
            }
        }
    }
}