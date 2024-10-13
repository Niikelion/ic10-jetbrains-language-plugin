package com.niikelion.ic10_language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class Ic10FileType: LanguageFileType(Ic10Language.Instance) {
    companion object{
        @JvmStatic
        val Instance = Ic10FileType()
    }

    override fun getName(): String = "Ic10 File"

    override fun getDescription(): String = "Ic10 language file from Stationeers game"

    override fun getDefaultExtension(): String = "ic10"

    override fun getIcon(): Icon = Ic10Icons.File
}