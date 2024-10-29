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

OPENBRACKET="HASH("
CLOSEBRACKET=")"
HASHCONTENT=\"[a-zA-Z0-9_ ]+\"

NAME=[a-zA-Z_][a-zA-Z0-9_]*
BINARY="%"[0-1](_?[0-1])*
DECIMAL=[0-9]+
HEXADECIMAL="$"[0-9A-F](_?[0-9A-F])*
FLOAT={DECIMAL}"."{DECIMAL}
MINUS="-"

COLON=":"
DOT="."

%state HASH_VALUE

%%

<YYINITIAL> {NAME} { yybegin(YYINITIAL); return Ic10Types.NAME; }
<YYINITIAL> {BINARY} { yybegin(YYINITIAL); return Ic10Types.BINARY; }
<YYINITIAL> {MINUS}?{DECIMAL} { yybegin(YYINITIAL); return Ic10Types.DECIMAL; }
<YYINITIAL> {HEXADECIMAL} { yybegin(YYINITIAL); return Ic10Types.HEXADECIMAL; }
<YYINITIAL> {MINUS}?{FLOAT} { yybegin(YYINITIAL); return Ic10Types.FLOAT; }
<YYINITIAL> {COLON} { yybegin(YYINITIAL); return Ic10Types.COLON; }
<YYINITIAL> {DOT} { yybegin(YYINITIAL); return Ic10Types.DOT; }

<YYINITIAL> {COMMENT} { yybegin(YYINITIAL); return Ic10Types.COMMENT; }

<YYINITIAL> {WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {CRLF} { yybegin(YYINITIAL); return Ic10Types.CRLF; }

<YYINITIAL> {OPENBRACKET} { yybegin(HASH_VALUE); return Ic10Types.OPENBRACKET; }
<HASH_VALUE> {CLOSEBRACKET} { yybegin(YYINITIAL); return Ic10Types.CLOSEBRACKET; }
<HASH_VALUE> {HASHCONTENT} { yybegin(HASH_VALUE); return Ic10Types.HASHCONTENT; }

[^] { return TokenType.BAD_CHARACTER; }