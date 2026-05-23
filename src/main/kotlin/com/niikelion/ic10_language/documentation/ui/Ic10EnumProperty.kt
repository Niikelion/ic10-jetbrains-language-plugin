package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.logic.Enum
import com.niikelion.ic10_language.psi.Ic10EnumProperty
import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.utils.toPrettyString

fun Ic10EnumProperty.doc(enum: Enum, prop: Enum.Value): Content = {
    definition {
        pre {
            separated(" ") {
                keyword("constant")
                text("${enum.name}.${text}")
                operator("=")
                number(prop.value.toPrettyString())
            }
        }
    }
    sectionsTable {
        if (prop.deprecated) {
            sectionContent {
                text("Deprecated")
            }
        }
        if (prop.description.isNotEmpty()) {
            sectionContent {
                text(prop.description)
            }
        }
    }
}