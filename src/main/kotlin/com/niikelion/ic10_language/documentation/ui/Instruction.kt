package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.logic.Instruction
import com.niikelion.ic10_language.ui.html.Content

val Instruction.doc: Content get() = {
    definition {
        pre {
            separated(" ") {
                keyword(name)
                arguments.forEach { arg ->
                    span {
                        text("${arg.name}(")
                        separated({ grayed { text("|") } }) {
                            arg.type.typeNames.forEach {
                                local(it)
                            }
                        }
                        text(")")
                    }
                }
            }
        }
    }
    sectionsTable {
        sectionContent {
            text(description)
        }
    }
}