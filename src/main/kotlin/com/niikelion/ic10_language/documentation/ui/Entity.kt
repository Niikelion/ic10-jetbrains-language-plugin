package com.niikelion.ic10_language.documentation.ui

import com.niikelion.ic10_language.logic.*
import com.niikelion.ic10_language.psi.Ic10Value
import com.niikelion.ic10_language.ui.html.Content
import com.niikelion.ic10_language.ui.html.psi.html

val Entity.doc get(): Content = {
    definition {
        pre {
            separated(" ") {
                val value = getValue()
                if (builtIn) {
                    value?.also {
                        keyword(when (it) {
                            is RegisterValue -> "register"
                            is RegisterReferenceValue -> "register reference"
                            is DeviceValue -> "device"
                            is DeviceReferenceValue -> "device reference"
                            is ChannelValue -> "channel"
                            is NumberValue -> "constant"
                            is NameValue -> "label"
                        })
                    }
                    text(name)
                    if (value is NumberValue) {
                        operator("=")
                        number(value.value.toPrettyString())
                    }
                } else {
                    keyword(if (value is NumberValue) "define" else "alias")
                    text(name)

                    references?.run(Ic10Value::html) ?: (value as? NumberValue)?.run {
                        number(this@run.value.toPrettyString())
                    }
                }
            }
        }
    }
}

private fun Double.toPrettyString(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()