package com.niikelion.ic10_language.ui.html.psi

import com.niikelion.ic10_language.psi.Ic10Constant
import com.niikelion.ic10_language.ui.html.Content

val Ic10Constant.html: Content get() = {
    span {
        global(constantNameList[0].text)
        operator(".")
        text(constantNameList[1].text)
    }
}