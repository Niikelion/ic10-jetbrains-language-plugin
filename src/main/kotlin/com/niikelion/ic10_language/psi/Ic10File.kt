package com.niikelion.ic10_language.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.niikelion.ic10_language.Ic10Language
import com.niikelion.ic10_language.Ic10FileType

class Ic10File(viewProvider: FileViewProvider): PsiFileBase(viewProvider, Ic10Language.Instance) {
    override fun getFileType(): FileType = Ic10FileType.Instance
    override fun toString(): String = "Ic10 File"
}