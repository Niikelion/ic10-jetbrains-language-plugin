package com.niikelion.ic10_language.psi

import com.intellij.psi.tree.TokenSet

object Ic10TokenSets {
    val COMMENTS = TokenSet.create(Ic10Types.COMMENT)
    val NAMES = TokenSet.create(Ic10Types.REFERENCE_NAME)
}
