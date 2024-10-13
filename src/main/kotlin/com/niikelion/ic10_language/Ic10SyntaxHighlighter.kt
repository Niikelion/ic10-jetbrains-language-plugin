package com.niikelion.ic10_language

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.jetbrains.rd.generator.nova.array
import com.niikelion.ic10_language.psi.Ic10Types


class Ic10SyntaxHighlighter: SyntaxHighlighterBase() {
    companion object {
        val COMMENT = createTextAttributesKey("IC10_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val INSTRUCTION = createTextAttributesKey("IC10_INSTRUCTION", DefaultLanguageHighlighterColors.STATIC_METHOD)
        val NAME = createTextAttributesKey("IC10_NAME", DefaultLanguageHighlighterColors.STATIC_FIELD)
        val NUMBER = createTextAttributesKey("IC10_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val TEXT = createTextAttributesKey("IC10_TEXT", DefaultLanguageHighlighterColors.STRING)
        val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("IC10_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val INSTRUCTION_KEYS = arrayOf(INSTRUCTION)
        private val NAME_KEYS = arrayOf(NAME)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val TEXT_KEYS = arrayOf(TEXT)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = Ic10LexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        if (operators.contains(tokenType)) return INSTRUCTION_KEYS

        return when (tokenType) {
            Ic10Types.COMMENT -> COMMENT_KEYS

            Ic10Types.NAME -> NAME_KEYS
            Ic10Types.LABEL -> NUMBER_KEYS

            Ic10Types.HASHCONTENT -> TEXT_KEYS

            Ic10Types.DECIMAL, Ic10Types.FLOAT -> NUMBER_KEYS

            TokenType.BAD_CHARACTER -> BAD_CHARACTER_KEYS
            else -> EMPTY_KEYS
        }
    }
}

val operators = arrayOf(
    Ic10Types.OP_HCF,
    Ic10Types.OP_YIELD,
    Ic10Types.OP_SLEEP,
    Ic10Types.OP_ALIAS,
    Ic10Types.OP_DEFINE,
    Ic10Types.OP_MOVE,
    Ic10Types.OP_ADD,
    Ic10Types.OP_SUB,
    Ic10Types.OP_MUL,
    Ic10Types.OP_DIV,
    Ic10Types.OP_NOT,
    Ic10Types.OP_AND,
    Ic10Types.OP_OR,
    Ic10Types.OP_XOR
)