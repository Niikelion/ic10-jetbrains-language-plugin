package com.niikelion.ic10_language

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.niikelion.ic10_language.psi.Ic10Types


class Ic10SyntaxHighlighter: SyntaxHighlighterBase() {
    companion object {
        private val COMMENT = createTextAttributesKey("IC10_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        private val INSTRUCTION = createTextAttributesKey("IC10_INSTRUCTION", DefaultLanguageHighlighterColors.STATIC_METHOD)
        private val NAME = createTextAttributesKey("IC10_NAME", DefaultLanguageHighlighterColors.STATIC_FIELD)
        private val NUMBER = createTextAttributesKey("IC10_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        private val HASH = createTextAttributesKey("IC10_HASH", DefaultLanguageHighlighterColors.STRING)
        private val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("IC10_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val INSTRUCTION_KEYS = arrayOf(INSTRUCTION)
        private val NAME_KEYS = arrayOf(NAME)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val HASH_KEYS = arrayOf(HASH)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = Ic10LexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            Ic10Types.COMMENT -> COMMENT_KEYS

            Ic10Types.NAME -> NAME_KEYS
            Ic10Types.LABEL -> NUMBER_KEYS

            Ic10Types.OP_ABS,
            Ic10Types.OP_ADD,
            Ic10Types.OP_ALIAS,
            Ic10Types.OP_AND,
            Ic10Types.OP_ACOS,
            Ic10Types.OP_ASIN,
            Ic10Types.OP_ATAN,
            Ic10Types.OP_ATAN2,
            Ic10Types.OP_BAP,
            Ic10Types.OP_BAPAL,
            Ic10Types.OP_BAPZ,
            Ic10Types.OP_BAPZAL,
            Ic10Types.OP_BDNS,
            Ic10Types.OP_BDNSAL,
            Ic10Types.OP_BDSE,
            Ic10Types.OP_BDSEAL,
            Ic10Types.OP_BEQ,
            Ic10Types.OP_BEQAL,
            Ic10Types.OP_BEQZ,
            Ic10Types.OP_BEQZAL,
            Ic10Types.OP_BGE,
            Ic10Types.OP_BGEAL,
            Ic10Types.OP_BGEZ,
            Ic10Types.OP_BGEZAL,
            Ic10Types.OP_BGT,
            Ic10Types.OP_BGTAL,
            Ic10Types.OP_BGTZ,
            Ic10Types.OP_BGTZAL,
            Ic10Types.OP_BLE,
            Ic10Types.OP_BLEAL,
            Ic10Types.OP_BLEZ,
            Ic10Types.OP_BLEZAL,
            Ic10Types.OP_BLT,
            Ic10Types.OP_BLTAL,
            Ic10Types.OP_BLTZ,
            Ic10Types.OP_BLTZAL,
            Ic10Types.OP_BNA,
            Ic10Types.OP_BNAAL,
            Ic10Types.OP_BNAN,
            Ic10Types.OP_BNAZ,
            Ic10Types.OP_BNAZAL,
            Ic10Types.OP_BNE,
            Ic10Types.OP_BNEAL,
            Ic10Types.OP_BNEZ,
            Ic10Types.OP_BNEZAL,
            Ic10Types.OP_BRAP,
            Ic10Types.OP_BRAPZ,
            Ic10Types.OP_BRDNS,
            Ic10Types.OP_BRDSE,
            Ic10Types.OP_BREQ,
            Ic10Types.OP_BREQZ,
            Ic10Types.OP_BRGE,
            Ic10Types.OP_BRGEZ,
            Ic10Types.OP_BRGT,
            Ic10Types.OP_BRGTZ,
            Ic10Types.OP_BRLE,
            Ic10Types.OP_BRLEZ,
            Ic10Types.OP_BRLT,
            Ic10Types.OP_BRLTZ,
            Ic10Types.OP_BRNA,
            Ic10Types.OP_BRNAN,
            Ic10Types.OP_BRNAZ,
            Ic10Types.OP_BRNE,
            Ic10Types.OP_BRNEZ,
            Ic10Types.OP_CEIL,
            Ic10Types.OP_CLR,
            Ic10Types.OP_CLRD,
            Ic10Types.OP_COS,
            Ic10Types.OP_DEFINE,
            Ic10Types.OP_DIV,
            Ic10Types.OP_EXP,
            Ic10Types.OP_FLOOR,
            Ic10Types.OP_GET,
            Ic10Types.OP_GETD,
            Ic10Types.OP_HCF,
            Ic10Types.OP_J,
            Ic10Types.OP_JAL,
            Ic10Types.OP_JR,
            Ic10Types.OP_L,
            Ic10Types.OP_LB,
            Ic10Types.OP_LBN,
            Ic10Types.OP_LBNS,
            Ic10Types.OP_LBS,
            Ic10Types.OP_LD,
            Ic10Types.OP_LOG,
            Ic10Types.OP_LR,
            Ic10Types.OP_LS,
            Ic10Types.OP_MAX,
            Ic10Types.OP_MIN,
            Ic10Types.OP_MOD,
            Ic10Types.OP_MOVE,
            Ic10Types.OP_MUL,
            Ic10Types.OP_NOR,
            Ic10Types.OP_NOT,
            Ic10Types.OP_OR,
            Ic10Types.OP_PEEK,
            Ic10Types.OP_POKE,
            Ic10Types.OP_POP,
            Ic10Types.OP_PUSH,
            Ic10Types.OP_PUT,
            Ic10Types.OP_PUTD,
            Ic10Types.OP_RAND,
            Ic10Types.OP_RMAP,
            Ic10Types.OP_ROUND,
            Ic10Types.OP_S,
            Ic10Types.OP_SAP,
            Ic10Types.OP_SAPZ,
            Ic10Types.OP_SB,
            Ic10Types.OP_SBN,
            Ic10Types.OP_SBS,
            Ic10Types.OP_SD,
            Ic10Types.OP_SDNS,
            Ic10Types.OP_SDSE,
            Ic10Types.OP_SELECT,
            Ic10Types.OP_SEQ,
            Ic10Types.OP_SEQZ,
            Ic10Types.OP_SGE,
            Ic10Types.OP_SGEZ,
            Ic10Types.OP_SGT,
            Ic10Types.OP_SGTZ,
            Ic10Types.OP_SIN,
            Ic10Types.OP_SLA,
            Ic10Types.OP_SLE,
            Ic10Types.OP_SLEEP,
            Ic10Types.OP_SLEZ,
            Ic10Types.OP_SLL,
            Ic10Types.OP_SLT,
            Ic10Types.OP_SLTZ,
            Ic10Types.OP_SNA,
            Ic10Types.OP_SNAN,
            Ic10Types.OP_SNANZ,
            Ic10Types.OP_SNAZ,
            Ic10Types.OP_SNE,
            Ic10Types.OP_SNEZ,
            Ic10Types.OP_SQRT,
            Ic10Types.OP_SRA,
            Ic10Types.OP_SRL,
            Ic10Types.OP_SS,
            Ic10Types.OP_SUB,
            Ic10Types.OP_TAN,
            Ic10Types.OP_TRUNC,
            Ic10Types.OP_XOR,
            Ic10Types.OP_YIELD
                -> INSTRUCTION_KEYS

            Ic10Types.HASHCONTENT -> HASH_KEYS

            Ic10Types.DECIMAL, Ic10Types.FLOAT -> NUMBER_KEYS

            TokenType.BAD_CHARACTER -> BAD_CHARACTER_KEYS
            else -> EMPTY_KEYS
        }
    }
}