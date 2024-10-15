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

COLON=":"

OP_ABS="abs"
OP_ADD="add"
OP_ALIAS="alias"
OP_AND="and"
OP_ACOS="acos"
OP_ASIN="asin"
OP_ATAN="atan"
OP_ATAN2="atan2"
OP_BAP="bap"
OP_BAPAL="bapal"
OP_BAPZ="bapz"
OP_BAPZAL="bapzal"
OP_BDNS="bdns"
OP_BDNSAL="bdnsal"
OP_BDSE="bdse"
OP_BDSEAL="bdseal"
OP_BEQ="beq"
OP_BEQAL="beqal"
OP_BEQZ="beqz"
OP_BEQZAL="beqzal"
OP_BGE="bge"
OP_BGEAL="bgeal"
OP_BGEZ="bgez"
OP_BGEZAL="bgezal"
OP_BGT="bgt"
OP_BGTAL="bgtal"
OP_BGTZ="bgtz"
OP_BGTZAL="bgtzal"
OP_BLE="ble"
OP_BLEAL="bleal"
OP_BLEZ="blez"
OP_BLEZAL="blezal"
OP_BLT="blt"
OP_BLTAL="bltal"
OP_BLTZ="bltz"
OP_BLTZAL="bltzal"
OP_BNA="bna"
OP_BNAAL="bnaal"
OP_BNAN="bnan"
OP_BNAZ="bnaz"
OP_BNAZAL="bnazal"
OP_BNE="bne"
OP_BNEAL="bneal"
OP_BNEZ="bnez"
OP_BNEZAL="bnezal"
OP_BRAP="brap"
OP_BRAPZ="brapz"
OP_BRDNS="brdns"
OP_BRDSE="brdse"
OP_BREQ="breq"
OP_BREQZ="breqz"
OP_BRGE="brge"
OP_BRGEZ="brgez"
OP_BRGT="brgt"
OP_BRGTZ="brgtz"
OP_BRLE="brle"
OP_BRLEZ="brlez"
OP_BRLT="brlt"
OP_BRLTZ="brltz"
OP_BRNA="brna"
OP_BRNAN="brnan"
OP_BRNAZ="brnaz"
OP_BRNE="brne"
OP_BRNEZ="brnez"
OP_CEIL="ceil"
OP_CLR="clr"
OP_CLRD="clrd"
OP_COS="cos"
OP_DEFINE="define"
OP_DIV="div"
OP_EXP="exp"
OP_FLOOR="floor"
OP_GET="get"
OP_GETD="getd"
OP_HCF="hcf"
OP_J="j"
OP_JAL="jal"
OP_JR="jr"
OP_L="l"
OP_LB="lb"
OP_LBN="lbn"
OP_LBNS="lbns"
OP_LBS="lbs"
OP_LD="ld"
OP_LOG="log"
OP_LR="lr"
OP_LS="ls"
OP_MAX="max"
OP_MIN="min"
OP_MOD="mod"
OP_MOVE="move"
OP_MUL="mul"
OP_NOR="nor"
OP_NOT="not"
OP_OR="or"
OP_PEEK="peek"
OP_POKE="poke"
OP_POP="pop"
OP_PUSH="push"
OP_PUT="put"
OP_PUTD="putd"
OP_RAND="rand"
OP_RMAP="rmap"
OP_ROUND="round"
OP_S="s"
OP_SAP="sap"
OP_SAPZ="sapz"
OP_SB="sb"
OP_SBN="sbn"
OP_SBS="sbs"
OP_SD="sd"
OP_SDNS="sdns"
OP_SDSE="sdse"
OP_SELECT="select"
OP_SEQ="seq"
OP_SEQZ="seqz"
OP_SGE="sge"
OP_SGEZ="sgez"
OP_SGT="sgt"
OP_SGTZ="sgtz"
OP_SIN="sin"
OP_SLA="sla"
OP_SLE="sle"
OP_SLEEP="sleep"
OP_SLEZ="slez"
OP_SLL="sll"
OP_SLT="slt"
OP_SLTZ="sltz"
OP_SNA="sna"
OP_SNAN="snan"
OP_SNANZ="snanz"
OP_SNAZ="snaz"
OP_SNE="sne"
OP_SNEZ="snez"
OP_SQRT="sqrt"
OP_SRA="sra"
OP_SRL="srl"
OP_SS="ss"
OP_SUB="sub"
OP_TAN="tan"
OP_TRUNC="trunc"
OP_XOR="xor"
OP_YIELD="yield"

%state HASH_VALUE

%%

<YYINITIAL> {OP_ABS} { yybegin(YYINITIAL); return Ic10Types.OP_ABS; }
<YYINITIAL> {OP_ADD} { yybegin(YYINITIAL); return Ic10Types.OP_ADD; }
<YYINITIAL> {OP_ALIAS} { yybegin(YYINITIAL); return Ic10Types.OP_ALIAS; }
<YYINITIAL> {OP_AND} { yybegin(YYINITIAL); return Ic10Types.OP_AND; }
<YYINITIAL> {OP_ACOS} { yybegin(YYINITIAL); return Ic10Types.OP_ACOS; }
<YYINITIAL> {OP_ASIN} { yybegin(YYINITIAL); return Ic10Types.OP_ASIN; }
<YYINITIAL> {OP_ATAN} { yybegin(YYINITIAL); return Ic10Types.OP_ATAN; }
<YYINITIAL> {OP_ATAN2} { yybegin(YYINITIAL); return Ic10Types.OP_ATAN2; }
<YYINITIAL> {OP_BAP} { yybegin(YYINITIAL); return Ic10Types.OP_BAP; }
<YYINITIAL> {OP_BAPAL} { yybegin(YYINITIAL); return Ic10Types.OP_BAPAL; }
<YYINITIAL> {OP_BAPZ} { yybegin(YYINITIAL); return Ic10Types.OP_BAPZ; }
<YYINITIAL> {OP_BAPZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BAPZAL; }
<YYINITIAL> {OP_BDNS} { yybegin(YYINITIAL); return Ic10Types.OP_BDNS; }
<YYINITIAL> {OP_BDNSAL} { yybegin(YYINITIAL); return Ic10Types.OP_BDNSAL; }
<YYINITIAL> {OP_BDSE} { yybegin(YYINITIAL); return Ic10Types.OP_BDSE; }
<YYINITIAL> {OP_BDSEAL} { yybegin(YYINITIAL); return Ic10Types.OP_BDSEAL; }
<YYINITIAL> {OP_BEQ} { yybegin(YYINITIAL); return Ic10Types.OP_BEQ; }
<YYINITIAL> {OP_BEQAL} { yybegin(YYINITIAL); return Ic10Types.OP_BEQAL; }
<YYINITIAL> {OP_BEQZ} { yybegin(YYINITIAL); return Ic10Types.OP_BEQZ; }
<YYINITIAL> {OP_BEQZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BEQZAL; }
<YYINITIAL> {OP_BGE} { yybegin(YYINITIAL); return Ic10Types.OP_BGE; }
<YYINITIAL> {OP_BGEAL} { yybegin(YYINITIAL); return Ic10Types.OP_BGEAL; }
<YYINITIAL> {OP_BGEZ} { yybegin(YYINITIAL); return Ic10Types.OP_BGEZ; }
<YYINITIAL> {OP_BGEZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BGEZAL; }
<YYINITIAL> {OP_BGT} { yybegin(YYINITIAL); return Ic10Types.OP_BGT; }
<YYINITIAL> {OP_BGTAL} { yybegin(YYINITIAL); return Ic10Types.OP_BGTAL; }
<YYINITIAL> {OP_BGTZ} { yybegin(YYINITIAL); return Ic10Types.OP_BGTZ; }
<YYINITIAL> {OP_BGTZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BGTZAL; }
<YYINITIAL> {OP_BLE} { yybegin(YYINITIAL); return Ic10Types.OP_BLE; }
<YYINITIAL> {OP_BLEAL} { yybegin(YYINITIAL); return Ic10Types.OP_BLEAL; }
<YYINITIAL> {OP_BLEZ} { yybegin(YYINITIAL); return Ic10Types.OP_BLEZ; }
<YYINITIAL> {OP_BLEZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BLEZAL; }
<YYINITIAL> {OP_BLT} { yybegin(YYINITIAL); return Ic10Types.OP_BLT; }
<YYINITIAL> {OP_BLTAL} { yybegin(YYINITIAL); return Ic10Types.OP_BLTAL; }
<YYINITIAL> {OP_BLTZ} { yybegin(YYINITIAL); return Ic10Types.OP_BLTZ; }
<YYINITIAL> {OP_BLTZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BLTZAL; }
<YYINITIAL> {OP_BNA} { yybegin(YYINITIAL); return Ic10Types.OP_BNA; }
<YYINITIAL> {OP_BNAAL} { yybegin(YYINITIAL); return Ic10Types.OP_BNAAL; }
<YYINITIAL> {OP_BNAN} { yybegin(YYINITIAL); return Ic10Types.OP_BNAN; }
<YYINITIAL> {OP_BNAZ} { yybegin(YYINITIAL); return Ic10Types.OP_BNAZ; }
<YYINITIAL> {OP_BNAZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BNAZAL; }
<YYINITIAL> {OP_BNE} { yybegin(YYINITIAL); return Ic10Types.OP_BNE; }
<YYINITIAL> {OP_BNEAL} { yybegin(YYINITIAL); return Ic10Types.OP_BNEAL; }
<YYINITIAL> {OP_BNEZ} { yybegin(YYINITIAL); return Ic10Types.OP_BNEZ; }
<YYINITIAL> {OP_BNEZAL} { yybegin(YYINITIAL); return Ic10Types.OP_BNEZAL; }
<YYINITIAL> {OP_BRAP} { yybegin(YYINITIAL); return Ic10Types.OP_BRAP; }
<YYINITIAL> {OP_BRAPZ} { yybegin(YYINITIAL); return Ic10Types.OP_BRAPZ; }
<YYINITIAL> {OP_BRDNS} { yybegin(YYINITIAL); return Ic10Types.OP_BRDNS; }
<YYINITIAL> {OP_BRDSE} { yybegin(YYINITIAL); return Ic10Types.OP_BRDSE; }
<YYINITIAL> {OP_BREQ} { yybegin(YYINITIAL); return Ic10Types.OP_BREQ; }
<YYINITIAL> {OP_BREQZ} { yybegin(YYINITIAL); return Ic10Types.OP_BREQZ; }
<YYINITIAL> {OP_BRGE} { yybegin(YYINITIAL); return Ic10Types.OP_BRGE; }
<YYINITIAL> {OP_BRGEZ} { yybegin(YYINITIAL); return Ic10Types.OP_BRGEZ; }
<YYINITIAL> {OP_BRGT} { yybegin(YYINITIAL); return Ic10Types.OP_BRGT; }
<YYINITIAL> {OP_BRGTZ} { yybegin(YYINITIAL); return Ic10Types.OP_BRGTZ; }
<YYINITIAL> {OP_BRLE} { yybegin(YYINITIAL); return Ic10Types.OP_BRLE; }
<YYINITIAL> {OP_BRLEZ} { yybegin(YYINITIAL); return Ic10Types.OP_BRLEZ; }
<YYINITIAL> {OP_BRLT} { yybegin(YYINITIAL); return Ic10Types.OP_BRLT; }
<YYINITIAL> {OP_BRLTZ} { yybegin(YYINITIAL); return Ic10Types.OP_BRLTZ; }
<YYINITIAL> {OP_BRNA} { yybegin(YYINITIAL); return Ic10Types.OP_BRNA; }
<YYINITIAL> {OP_BRNAN} { yybegin(YYINITIAL); return Ic10Types.OP_BRNAN; }
<YYINITIAL> {OP_BRNAZ} { yybegin(YYINITIAL); return Ic10Types.OP_BRNAZ; }
<YYINITIAL> {OP_BRNE} { yybegin(YYINITIAL); return Ic10Types.OP_BRNE; }
<YYINITIAL> {OP_BRNEZ} { yybegin(YYINITIAL); return Ic10Types.OP_BRNEZ; }
<YYINITIAL> {OP_CEIL} { yybegin(YYINITIAL); return Ic10Types.OP_CEIL; }
<YYINITIAL> {OP_CLR} { yybegin(YYINITIAL); return Ic10Types.OP_CLR; }
<YYINITIAL> {OP_CLRD} { yybegin(YYINITIAL); return Ic10Types.OP_CLRD; }
<YYINITIAL> {OP_COS} { yybegin(YYINITIAL); return Ic10Types.OP_COS; }
<YYINITIAL> {OP_DEFINE} { yybegin(YYINITIAL); return Ic10Types.OP_DEFINE; }
<YYINITIAL> {OP_DIV} { yybegin(YYINITIAL); return Ic10Types.OP_DIV; }
<YYINITIAL> {OP_EXP} { yybegin(YYINITIAL); return Ic10Types.OP_EXP; }
<YYINITIAL> {OP_FLOOR} { yybegin(YYINITIAL); return Ic10Types.OP_FLOOR; }
<YYINITIAL> {OP_GET} { yybegin(YYINITIAL); return Ic10Types.OP_GET; }
<YYINITIAL> {OP_GETD} { yybegin(YYINITIAL); return Ic10Types.OP_GETD; }
<YYINITIAL> {OP_HCF} { yybegin(YYINITIAL); return Ic10Types.OP_HCF; }
<YYINITIAL> {OP_J} { yybegin(YYINITIAL); return Ic10Types.OP_J; }
<YYINITIAL> {OP_JAL} { yybegin(YYINITIAL); return Ic10Types.OP_JAL; }
<YYINITIAL> {OP_JR} { yybegin(YYINITIAL); return Ic10Types.OP_JR; }
<YYINITIAL> {OP_L} { yybegin(YYINITIAL); return Ic10Types.OP_L; }
<YYINITIAL> {OP_LB} { yybegin(YYINITIAL); return Ic10Types.OP_LB; }
<YYINITIAL> {OP_LBN} { yybegin(YYINITIAL); return Ic10Types.OP_LBN; }
<YYINITIAL> {OP_LBNS} { yybegin(YYINITIAL); return Ic10Types.OP_LBNS; }
<YYINITIAL> {OP_LBS} { yybegin(YYINITIAL); return Ic10Types.OP_LBS; }
<YYINITIAL> {OP_LD} { yybegin(YYINITIAL); return Ic10Types.OP_LD; }
<YYINITIAL> {OP_LOG} { yybegin(YYINITIAL); return Ic10Types.OP_LOG; }
<YYINITIAL> {OP_LR} { yybegin(YYINITIAL); return Ic10Types.OP_LR; }
<YYINITIAL> {OP_LS} { yybegin(YYINITIAL); return Ic10Types.OP_LS; }
<YYINITIAL> {OP_MAX} { yybegin(YYINITIAL); return Ic10Types.OP_MAX; }
<YYINITIAL> {OP_MIN} { yybegin(YYINITIAL); return Ic10Types.OP_MIN; }
<YYINITIAL> {OP_MOD} { yybegin(YYINITIAL); return Ic10Types.OP_MOD; }
<YYINITIAL> {OP_MOVE} { yybegin(YYINITIAL); return Ic10Types.OP_MOVE; }
<YYINITIAL> {OP_MUL} { yybegin(YYINITIAL); return Ic10Types.OP_MUL; }
<YYINITIAL> {OP_NOR} { yybegin(YYINITIAL); return Ic10Types.OP_NOR; }
<YYINITIAL> {OP_NOT} { yybegin(YYINITIAL); return Ic10Types.OP_NOT; }
<YYINITIAL> {OP_OR} { yybegin(YYINITIAL); return Ic10Types.OP_OR; }
<YYINITIAL> {OP_PEEK} { yybegin(YYINITIAL); return Ic10Types.OP_PEEK; }
<YYINITIAL> {OP_POKE} { yybegin(YYINITIAL); return Ic10Types.OP_POKE; }
<YYINITIAL> {OP_POP} { yybegin(YYINITIAL); return Ic10Types.OP_POP; }
<YYINITIAL> {OP_PUSH} { yybegin(YYINITIAL); return Ic10Types.OP_PUSH; }
<YYINITIAL> {OP_PUT} { yybegin(YYINITIAL); return Ic10Types.OP_PUT; }
<YYINITIAL> {OP_PUTD} { yybegin(YYINITIAL); return Ic10Types.OP_PUTD; }
<YYINITIAL> {OP_RAND} { yybegin(YYINITIAL); return Ic10Types.OP_RAND; }
<YYINITIAL> {OP_RMAP} { yybegin(YYINITIAL); return Ic10Types.OP_RMAP; }
<YYINITIAL> {OP_ROUND} { yybegin(YYINITIAL); return Ic10Types.OP_ROUND; }
<YYINITIAL> {OP_S} { yybegin(YYINITIAL); return Ic10Types.OP_S; }
<YYINITIAL> {OP_SAP} { yybegin(YYINITIAL); return Ic10Types.OP_SAP; }
<YYINITIAL> {OP_SAPZ} { yybegin(YYINITIAL); return Ic10Types.OP_SAPZ; }
<YYINITIAL> {OP_SB} { yybegin(YYINITIAL); return Ic10Types.OP_SB; }
<YYINITIAL> {OP_SBN} { yybegin(YYINITIAL); return Ic10Types.OP_SBN; }
<YYINITIAL> {OP_SBS} { yybegin(YYINITIAL); return Ic10Types.OP_SBS; }
<YYINITIAL> {OP_SD} { yybegin(YYINITIAL); return Ic10Types.OP_SD; }
<YYINITIAL> {OP_SDNS} { yybegin(YYINITIAL); return Ic10Types.OP_SDNS; }
<YYINITIAL> {OP_SDSE} { yybegin(YYINITIAL); return Ic10Types.OP_SDSE; }
<YYINITIAL> {OP_SELECT} { yybegin(YYINITIAL); return Ic10Types.OP_SELECT; }
<YYINITIAL> {OP_SEQ} { yybegin(YYINITIAL); return Ic10Types.OP_SEQ; }
<YYINITIAL> {OP_SEQZ} { yybegin(YYINITIAL); return Ic10Types.OP_SEQZ; }
<YYINITIAL> {OP_SGE} { yybegin(YYINITIAL); return Ic10Types.OP_SGE; }
<YYINITIAL> {OP_SGEZ} { yybegin(YYINITIAL); return Ic10Types.OP_SGEZ; }
<YYINITIAL> {OP_SGT} { yybegin(YYINITIAL); return Ic10Types.OP_SGT; }
<YYINITIAL> {OP_SGTZ} { yybegin(YYINITIAL); return Ic10Types.OP_SGTZ; }
<YYINITIAL> {OP_SIN} { yybegin(YYINITIAL); return Ic10Types.OP_SIN; }
<YYINITIAL> {OP_SLA} { yybegin(YYINITIAL); return Ic10Types.OP_SLA; }
<YYINITIAL> {OP_SLE} { yybegin(YYINITIAL); return Ic10Types.OP_SLE; }
<YYINITIAL> {OP_SLEEP} { yybegin(YYINITIAL); return Ic10Types.OP_SLEEP; }
<YYINITIAL> {OP_SLEZ} { yybegin(YYINITIAL); return Ic10Types.OP_SLEZ; }
<YYINITIAL> {OP_SLL} { yybegin(YYINITIAL); return Ic10Types.OP_SLL; }
<YYINITIAL> {OP_SLT} { yybegin(YYINITIAL); return Ic10Types.OP_SLT; }
<YYINITIAL> {OP_SLTZ} { yybegin(YYINITIAL); return Ic10Types.OP_SLTZ; }
<YYINITIAL> {OP_SNA} { yybegin(YYINITIAL); return Ic10Types.OP_SNA; }
<YYINITIAL> {OP_SNAN} { yybegin(YYINITIAL); return Ic10Types.OP_SNAN; }
<YYINITIAL> {OP_SNANZ} { yybegin(YYINITIAL); return Ic10Types.OP_SNANZ; }
<YYINITIAL> {OP_SNAZ} { yybegin(YYINITIAL); return Ic10Types.OP_SNAZ; }
<YYINITIAL> {OP_SNE} { yybegin(YYINITIAL); return Ic10Types.OP_SNE; }
<YYINITIAL> {OP_SNEZ} { yybegin(YYINITIAL); return Ic10Types.OP_SNEZ; }
<YYINITIAL> {OP_SQRT} { yybegin(YYINITIAL); return Ic10Types.OP_SQRT; }
<YYINITIAL> {OP_SRA} { yybegin(YYINITIAL); return Ic10Types.OP_SRA; }
<YYINITIAL> {OP_SRL} { yybegin(YYINITIAL); return Ic10Types.OP_SRL; }
<YYINITIAL> {OP_SS} { yybegin(YYINITIAL); return Ic10Types.OP_SS; }
<YYINITIAL> {OP_SUB} { yybegin(YYINITIAL); return Ic10Types.OP_SUB; }
<YYINITIAL> {OP_TAN} { yybegin(YYINITIAL); return Ic10Types.OP_TAN; }
<YYINITIAL> {OP_TRUNC} { yybegin(YYINITIAL); return Ic10Types.OP_TRUNC; }
<YYINITIAL> {OP_XOR} { yybegin(YYINITIAL); return Ic10Types.OP_XOR; }
<YYINITIAL> {OP_YIELD} { yybegin(YYINITIAL); return Ic10Types.OP_YIELD; }

<YYINITIAL> {NAME} { yybegin(YYINITIAL); return Ic10Types.NAME; }
<YYINITIAL> {BINARY} { yybegin(YYINITIAL); return Ic10Types.BINARY; }
<YYINITIAL> {DECIMAL} { yybegin(YYINITIAL); return Ic10Types.DECIMAL; }
<YYINITIAL> {HEXADECIMAL} { yybegin(YYINITIAL); return Ic10Types.HEXADECIMAL; }
<YYINITIAL> {FLOAT} { yybegin(YYINITIAL); return Ic10Types.FLOAT; }
<YYINITIAL> {COLON} { yybegin(YYINITIAL); return Ic10Types.COLON; }

<YYINITIAL> {COMMENT} { yybegin(YYINITIAL); return Ic10Types.COMMENT; }

<YYINITIAL> {WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {CRLF} { yybegin(YYINITIAL); return Ic10Types.CRLF; }

<YYINITIAL> {OPENBRACKET} { yybegin(HASH_VALUE); return Ic10Types.OPENBRACKET; }
<HASH_VALUE> {CLOSEBRACKET} { yybegin(YYINITIAL); return Ic10Types.CLOSEBRACKET; }
<HASH_VALUE> {HASHCONTENT} { yybegin(HASH_VALUE); return Ic10Types.HASHCONTENT; }

[^] { return TokenType.BAD_CHARACTER; }