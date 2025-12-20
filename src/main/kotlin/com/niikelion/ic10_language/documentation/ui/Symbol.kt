package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.Ic10Symbol
import com.niikelion.ic10_language.Ic10SymbolType
import com.niikelion.ic10_language.psi.Ic10Value
import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.ui.html.psi.html

val Ic10Symbol.doc: Content get() = {
    definition {
        pre {
            separated(" ") {
                if (isBuiltIn) {
                    keyword(when (type) {
                        Ic10SymbolType.Constant -> "value"
                        Ic10SymbolType.Variable -> "variable"
                        Ic10SymbolType.Register -> "register"
                        Ic10SymbolType.Device -> "device"
                    })
                    text(name)
                } else {
                    keyword(if (type === Ic10SymbolType.Constant) "define" else "alias")
                    text(definitionElement.text)

                    if (valueElement is Ic10Value)
                        render(valueElement.html)
                }
            }
        }
    }
}