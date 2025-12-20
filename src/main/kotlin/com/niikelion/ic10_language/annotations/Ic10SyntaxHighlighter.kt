package com.niikelion.ic10_language.annotations

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.niikelion.ic10_language.Ic10LexerAdapter
import com.niikelion.ic10_language.psi.Ic10Types


class Ic10SyntaxHighlighter: SyntaxHighlighterBase() {
    companion object {
        val INSTRUCTION = createTextAttributesKey("IC10_INSTRUCTION", DefaultLanguageHighlighterColors.KEYWORD)
        val LABEL = createTextAttributesKey("IC10_LABEL", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val CONSTANT = createTextAttributesKey("IC10_CONSTANT", DefaultLanguageHighlighterColors.STATIC_FIELD)
        private val COMMENT = createTextAttributesKey("IC10_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        private val NAME = createTextAttributesKey("IC10_NAME", DefaultLanguageHighlighterColors.IDENTIFIER)
        val NUMBER = createTextAttributesKey("IC10_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val MACRO = createTextAttributesKey("IC10_MACRO", DefaultLanguageHighlighterColors.STRING)
        private val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("IC10_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val NAME_KEYS = arrayOf(NAME)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val MACRO_KEYS = arrayOf(MACRO)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = Ic10LexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            Ic10Types.COMMENT -> COMMENT_KEYS

            Ic10Types.NAME -> NAME_KEYS

            Ic10Types.MACRO_CONTENT -> MACRO_KEYS

            Ic10Types.DECIMAL, Ic10Types.FLOAT, Ic10Types.HEXADECIMAL, Ic10Types.BINARY -> NUMBER_KEYS

            TokenType.BAD_CHARACTER -> BAD_CHARACTER_KEYS
            else -> EMPTY_KEYS
        }
    }
}