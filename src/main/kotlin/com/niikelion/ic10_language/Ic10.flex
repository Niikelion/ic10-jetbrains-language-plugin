package com.niikelion.ic10_language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.niikelion.ic10_language.psi.Ic10Types;
import com.intellij.psi.TokenType;

%%

%class Ic10Lexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \t]
COMMENT=("#")[^\r\n]*

HASHSTART='HASH\(\"'
HASHEND='\"\)'
HASHCONTENT=[a-zA-Z0-9_ ]+

NAME=[a-zA-Z_][a-zA-Z0-9_]*
DECIMAL=[0-9]+
FLOAT={DECIMAL}"."{DECIMAL}

COLON=":"

OP_HCF="hcf"
OP_YIELD="yield"
OP_SLEEP="sleep"
OP_MOVE="move"
OP_DEFINE="define"
OP_ALIAS="alias"
OP_ADD="add"
OP_SUB="sub"
OP_MUL="mul"
OP_DIV="div"
OP_MOD="mod"
OP_NOT="not"
OP_AND="and"
OP_OR="or"
OP_XOR="xor"

%state HASH_VALUE

%%

<YYINITIAL> {OP_HCF} { yybegin(YYINITIAL); return Ic10Types.OP_HCF; }
<YYINITIAL> {OP_YIELD} { yybegin(YYINITIAL); return Ic10Types.OP_YIELD; }
<YYINITIAL> {OP_SLEEP} { yybegin(YYINITIAL); return Ic10Types.OP_SLEEP; }
<YYINITIAL> {OP_MOVE} { yybegin(YYINITIAL); return Ic10Types.OP_MOVE; }
<YYINITIAL> {OP_DEFINE} { yybegin(YYINITIAL); return Ic10Types.OP_DEFINE; }
<YYINITIAL> {OP_ALIAS} { yybegin(YYINITIAL); return Ic10Types.OP_ALIAS; }
<YYINITIAL> {OP_ADD} { yybegin(YYINITIAL); return Ic10Types.OP_ADD; }
<YYINITIAL> {OP_SUB} { yybegin(YYINITIAL); return Ic10Types.OP_SUB; }
<YYINITIAL> {OP_MUL} { yybegin(YYINITIAL); return Ic10Types.OP_MUL; }
<YYINITIAL> {OP_DIV} { yybegin(YYINITIAL); return Ic10Types.OP_DIV; }
<YYINITIAL> {OP_MOD} { yybegin(YYINITIAL); return Ic10Types.OP_MOD; }
<YYINITIAL> {OP_NOT} { yybegin(YYINITIAL); return Ic10Types.OP_NOT; }
<YYINITIAL> {OP_AND} { yybegin(YYINITIAL); return Ic10Types.OP_AND; }
<YYINITIAL> {OP_OR} { yybegin(YYINITIAL); return Ic10Types.OP_OR; }
<YYINITIAL> {OP_XOR} { yybegin(YYINITIAL); return Ic10Types.OP_XOR; }

<YYINITIAL> {NAME} { yybegin(YYINITIAL); return Ic10Types.NAME; }
<YYINITIAL> {DECIMAL} { yybegin(YYINITIAL); return Ic10Types.DECIMAL; }
<YYINITIAL> {FLOAT} { yybegin(YYINITIAL); return Ic10Types.FLOAT; }
<YYINITIAL> {COLON} { yybegin(YYINITIAL); return Ic10Types.COLON; }

<YYINITIAL> {COMMENT} { yybegin(YYINITIAL); return Ic10Types.COMMENT; }

<YYINITIAL> {WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {CRLF} { yybegin(YYINITIAL); return Ic10Types.CRLF; }

<YYINITIAL> {HASHSTART} { yybegin(HASH_VALUE); return Ic10Types.HASHSTART; }
<HASH_VALUE> {HASHCONTENT} { yybegin(HASH_VALUE); return Ic10Types.HASHCONTENT; }
<HASH_VALUE> {HASHEND} { yybegin(YYINITIAL); return Ic10Types.HASHEND; }

[^] { return TokenType.BAD_CHARACTER; }