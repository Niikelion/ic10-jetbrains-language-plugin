// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.niikelion.ic10_language.psi.impl.*;

public interface Ic10Types {

  IElementType ALIAS_OP = new Ic10ElementType("ALIAS_OP");
  IElementType BIN_OP = new Ic10ElementType("BIN_OP");
  IElementType BIN_OP_NAME = new Ic10ElementType("BIN_OP_NAME");
  IElementType BRANCH_OP_1 = new Ic10ElementType("BRANCH_OP_1");
  IElementType BRANCH_OP_1_NAME = new Ic10ElementType("BRANCH_OP_1_NAME");
  IElementType BRANCH_OP_2 = new Ic10ElementType("BRANCH_OP_2");
  IElementType BRANCH_OP_2_NAME = new Ic10ElementType("BRANCH_OP_2_NAME");
  IElementType BRANCH_OP_3 = new Ic10ElementType("BRANCH_OP_3");
  IElementType BRANCH_OP_3_NAME = new Ic10ElementType("BRANCH_OP_3_NAME");
  IElementType BRANCH_OP_4 = new Ic10ElementType("BRANCH_OP_4");
  IElementType BRANCH_OP_4_NAME = new Ic10ElementType("BRANCH_OP_4_NAME");
  IElementType BRANCH_OP_DEV = new Ic10ElementType("BRANCH_OP_DEV");
  IElementType BRANCH_OP_DEV_NAME = new Ic10ElementType("BRANCH_OP_DEV_NAME");
  IElementType CLRD_OP = new Ic10ElementType("CLRD_OP");
  IElementType CLR_OP = new Ic10ElementType("CLR_OP");
  IElementType DEFINE_OP = new Ic10ElementType("DEFINE_OP");
  IElementType DEVICE = new Ic10ElementType("DEVICE");
  IElementType GET_D_OP = new Ic10ElementType("GET_D_OP");
  IElementType GET_OP = new Ic10ElementType("GET_OP");
  IElementType HASH = new Ic10ElementType("HASH");
  IElementType HCF_OP = new Ic10ElementType("HCF_OP");
  IElementType LABEL = new Ic10ElementType("LABEL");
  IElementType LBNS_OP = new Ic10ElementType("LBNS_OP");
  IElementType LBN_OP = new Ic10ElementType("LBN_OP");
  IElementType LBS_OP = new Ic10ElementType("LBS_OP");
  IElementType LB_OP = new Ic10ElementType("LB_OP");
  IElementType LD_OP = new Ic10ElementType("LD_OP");
  IElementType LINE = new Ic10ElementType("LINE");
  IElementType LR_OP = new Ic10ElementType("LR_OP");
  IElementType LS_OP = new Ic10ElementType("LS_OP");
  IElementType L_OP = new Ic10ElementType("L_OP");
  IElementType NUMBER = new Ic10ElementType("NUMBER");
  IElementType OPERATION = new Ic10ElementType("OPERATION");
  IElementType PEEK_OP = new Ic10ElementType("PEEK_OP");
  IElementType POKE_OP = new Ic10ElementType("POKE_OP");
  IElementType POP_OP = new Ic10ElementType("POP_OP");
  IElementType PUSH_OP = new Ic10ElementType("PUSH_OP");
  IElementType PUTD_OP = new Ic10ElementType("PUTD_OP");
  IElementType PUT_OP = new Ic10ElementType("PUT_OP");
  IElementType RAND_OP = new Ic10ElementType("RAND_OP");
  IElementType RMAP_OP = new Ic10ElementType("RMAP_OP");
  IElementType SBN_OP = new Ic10ElementType("SBN_OP");
  IElementType SBS_OP = new Ic10ElementType("SBS_OP");
  IElementType SB_OP = new Ic10ElementType("SB_OP");
  IElementType SDNS_OP = new Ic10ElementType("SDNS_OP");
  IElementType SDSE_OP = new Ic10ElementType("SDSE_OP");
  IElementType SD_OP = new Ic10ElementType("SD_OP");
  IElementType SELECT_OP_1 = new Ic10ElementType("SELECT_OP_1");
  IElementType SELECT_OP_1_NAME = new Ic10ElementType("SELECT_OP_1_NAME");
  IElementType SELECT_OP_2 = new Ic10ElementType("SELECT_OP_2");
  IElementType SELECT_OP_2_NAME = new Ic10ElementType("SELECT_OP_2_NAME");
  IElementType SELECT_OP_3 = new Ic10ElementType("SELECT_OP_3");
  IElementType SELECT_OP_3_NAME = new Ic10ElementType("SELECT_OP_3_NAME");
  IElementType SLEEP_OP = new Ic10ElementType("SLEEP_OP");
  IElementType SS_OP = new Ic10ElementType("SS_OP");
  IElementType S_OP = new Ic10ElementType("S_OP");
  IElementType UN_OP = new Ic10ElementType("UN_OP");
  IElementType UN_OP_NAME = new Ic10ElementType("UN_OP_NAME");
  IElementType VALUE = new Ic10ElementType("VALUE");
  IElementType VARIABLE = new Ic10ElementType("VARIABLE");
  IElementType YIELD_OP = new Ic10ElementType("YIELD_OP");

  IElementType BINARY = new Ic10TokenType("BINARY");
  IElementType COLON = new Ic10TokenType("COLON");
  IElementType COMMENT = new Ic10TokenType("COMMENT");
  IElementType CRLF = new Ic10TokenType("CRLF");
  IElementType DECIMAL = new Ic10TokenType("DECIMAL");
  IElementType FLOAT = new Ic10TokenType("FLOAT");
  IElementType HASHCONTENT = new Ic10TokenType("HASHCONTENT");
  IElementType HASHEND = new Ic10TokenType("HASHEND");
  IElementType HASHSTART = new Ic10TokenType("HASHSTART");
  IElementType HEXADECIMAL = new Ic10TokenType("HEXADECIMAL");
  IElementType NAME = new Ic10TokenType("NAME");
  IElementType OP_ABS = new Ic10TokenType("OP_ABS");
  IElementType OP_ACOS = new Ic10TokenType("OP_ACOS");
  IElementType OP_ADD = new Ic10TokenType("OP_ADD");
  IElementType OP_ALIAS = new Ic10TokenType("OP_ALIAS");
  IElementType OP_AND = new Ic10TokenType("OP_AND");
  IElementType OP_ASIN = new Ic10TokenType("OP_ASIN");
  IElementType OP_ATAN = new Ic10TokenType("OP_ATAN");
  IElementType OP_ATAN2 = new Ic10TokenType("OP_ATAN2");
  IElementType OP_BAP = new Ic10TokenType("OP_BAP");
  IElementType OP_BAPAL = new Ic10TokenType("OP_BAPAL");
  IElementType OP_BAPZ = new Ic10TokenType("OP_BAPZ");
  IElementType OP_BAPZAL = new Ic10TokenType("OP_BAPZAL");
  IElementType OP_BDNS = new Ic10TokenType("OP_BDNS");
  IElementType OP_BDNSAL = new Ic10TokenType("OP_BDNSAL");
  IElementType OP_BDSE = new Ic10TokenType("OP_BDSE");
  IElementType OP_BDSEAL = new Ic10TokenType("OP_BDSEAL");
  IElementType OP_BEQ = new Ic10TokenType("OP_BEQ");
  IElementType OP_BEQAL = new Ic10TokenType("OP_BEQAL");
  IElementType OP_BEQZ = new Ic10TokenType("OP_BEQZ");
  IElementType OP_BEQZAL = new Ic10TokenType("OP_BEQZAL");
  IElementType OP_BGE = new Ic10TokenType("OP_BGE");
  IElementType OP_BGEAL = new Ic10TokenType("OP_BGEAL");
  IElementType OP_BGEZ = new Ic10TokenType("OP_BGEZ");
  IElementType OP_BGEZAL = new Ic10TokenType("OP_BGEZAL");
  IElementType OP_BGT = new Ic10TokenType("OP_BGT");
  IElementType OP_BGTAL = new Ic10TokenType("OP_BGTAL");
  IElementType OP_BGTZ = new Ic10TokenType("OP_BGTZ");
  IElementType OP_BGTZAL = new Ic10TokenType("OP_BGTZAL");
  IElementType OP_BLE = new Ic10TokenType("OP_BLE");
  IElementType OP_BLEAL = new Ic10TokenType("OP_BLEAL");
  IElementType OP_BLEZ = new Ic10TokenType("OP_BLEZ");
  IElementType OP_BLEZAL = new Ic10TokenType("OP_BLEZAL");
  IElementType OP_BLT = new Ic10TokenType("OP_BLT");
  IElementType OP_BLTAL = new Ic10TokenType("OP_BLTAL");
  IElementType OP_BLTZ = new Ic10TokenType("OP_BLTZ");
  IElementType OP_BLTZAL = new Ic10TokenType("OP_BLTZAL");
  IElementType OP_BNA = new Ic10TokenType("OP_BNA");
  IElementType OP_BNAAL = new Ic10TokenType("OP_BNAAL");
  IElementType OP_BNAN = new Ic10TokenType("OP_BNAN");
  IElementType OP_BNAZ = new Ic10TokenType("OP_BNAZ");
  IElementType OP_BNAZAL = new Ic10TokenType("OP_BNAZAL");
  IElementType OP_BNE = new Ic10TokenType("OP_BNE");
  IElementType OP_BNEAL = new Ic10TokenType("OP_BNEAL");
  IElementType OP_BNEZ = new Ic10TokenType("OP_BNEZ");
  IElementType OP_BNEZAL = new Ic10TokenType("OP_BNEZAL");
  IElementType OP_BRAP = new Ic10TokenType("OP_BRAP");
  IElementType OP_BRAPZ = new Ic10TokenType("OP_BRAPZ");
  IElementType OP_BRDNS = new Ic10TokenType("OP_BRDNS");
  IElementType OP_BRDSE = new Ic10TokenType("OP_BRDSE");
  IElementType OP_BREQ = new Ic10TokenType("OP_BREQ");
  IElementType OP_BREQZ = new Ic10TokenType("OP_BREQZ");
  IElementType OP_BRGE = new Ic10TokenType("OP_BRGE");
  IElementType OP_BRGEZ = new Ic10TokenType("OP_BRGEZ");
  IElementType OP_BRGT = new Ic10TokenType("OP_BRGT");
  IElementType OP_BRGTZ = new Ic10TokenType("OP_BRGTZ");
  IElementType OP_BRLE = new Ic10TokenType("OP_BRLE");
  IElementType OP_BRLEZ = new Ic10TokenType("OP_BRLEZ");
  IElementType OP_BRLT = new Ic10TokenType("OP_BRLT");
  IElementType OP_BRLTZ = new Ic10TokenType("OP_BRLTZ");
  IElementType OP_BRNA = new Ic10TokenType("OP_BRNA");
  IElementType OP_BRNAN = new Ic10TokenType("OP_BRNAN");
  IElementType OP_BRNAZ = new Ic10TokenType("OP_BRNAZ");
  IElementType OP_BRNE = new Ic10TokenType("OP_BRNE");
  IElementType OP_BRNEZ = new Ic10TokenType("OP_BRNEZ");
  IElementType OP_CEIL = new Ic10TokenType("OP_CEIL");
  IElementType OP_CLR = new Ic10TokenType("OP_CLR");
  IElementType OP_CLRD = new Ic10TokenType("OP_CLRD");
  IElementType OP_COS = new Ic10TokenType("OP_COS");
  IElementType OP_DEFINE = new Ic10TokenType("OP_DEFINE");
  IElementType OP_DIV = new Ic10TokenType("OP_DIV");
  IElementType OP_EXP = new Ic10TokenType("OP_EXP");
  IElementType OP_FLOOR = new Ic10TokenType("OP_FLOOR");
  IElementType OP_GET = new Ic10TokenType("OP_GET");
  IElementType OP_GETD = new Ic10TokenType("OP_GETD");
  IElementType OP_HCF = new Ic10TokenType("OP_HCF");
  IElementType OP_J = new Ic10TokenType("OP_J");
  IElementType OP_JAL = new Ic10TokenType("OP_JAL");
  IElementType OP_JR = new Ic10TokenType("OP_JR");
  IElementType OP_L = new Ic10TokenType("OP_L");
  IElementType OP_LB = new Ic10TokenType("OP_LB");
  IElementType OP_LBN = new Ic10TokenType("OP_LBN");
  IElementType OP_LBNS = new Ic10TokenType("OP_LBNS");
  IElementType OP_LBS = new Ic10TokenType("OP_LBS");
  IElementType OP_LD = new Ic10TokenType("OP_LD");
  IElementType OP_LOG = new Ic10TokenType("OP_LOG");
  IElementType OP_LR = new Ic10TokenType("OP_LR");
  IElementType OP_LS = new Ic10TokenType("OP_LS");
  IElementType OP_MAX = new Ic10TokenType("OP_MAX");
  IElementType OP_MIN = new Ic10TokenType("OP_MIN");
  IElementType OP_MOD = new Ic10TokenType("OP_MOD");
  IElementType OP_MOVE = new Ic10TokenType("OP_MOVE");
  IElementType OP_MUL = new Ic10TokenType("OP_MUL");
  IElementType OP_NOR = new Ic10TokenType("OP_NOR");
  IElementType OP_NOT = new Ic10TokenType("OP_NOT");
  IElementType OP_OR = new Ic10TokenType("OP_OR");
  IElementType OP_PEEK = new Ic10TokenType("OP_PEEK");
  IElementType OP_POKE = new Ic10TokenType("OP_POKE");
  IElementType OP_POP = new Ic10TokenType("OP_POP");
  IElementType OP_PUSH = new Ic10TokenType("OP_PUSH");
  IElementType OP_PUT = new Ic10TokenType("OP_PUT");
  IElementType OP_PUTD = new Ic10TokenType("OP_PUTD");
  IElementType OP_RAND = new Ic10TokenType("OP_RAND");
  IElementType OP_RMAP = new Ic10TokenType("OP_RMAP");
  IElementType OP_ROUND = new Ic10TokenType("OP_ROUND");
  IElementType OP_S = new Ic10TokenType("OP_S");
  IElementType OP_SAP = new Ic10TokenType("OP_SAP");
  IElementType OP_SAPZ = new Ic10TokenType("OP_SAPZ");
  IElementType OP_SB = new Ic10TokenType("OP_SB");
  IElementType OP_SBN = new Ic10TokenType("OP_SBN");
  IElementType OP_SBS = new Ic10TokenType("OP_SBS");
  IElementType OP_SD = new Ic10TokenType("OP_SD");
  IElementType OP_SDNS = new Ic10TokenType("OP_SDNS");
  IElementType OP_SDSE = new Ic10TokenType("OP_SDSE");
  IElementType OP_SELECT = new Ic10TokenType("OP_SELECT");
  IElementType OP_SEQ = new Ic10TokenType("OP_SEQ");
  IElementType OP_SEQZ = new Ic10TokenType("OP_SEQZ");
  IElementType OP_SGE = new Ic10TokenType("OP_SGE");
  IElementType OP_SGEZ = new Ic10TokenType("OP_SGEZ");
  IElementType OP_SGT = new Ic10TokenType("OP_SGT");
  IElementType OP_SGTZ = new Ic10TokenType("OP_SGTZ");
  IElementType OP_SIN = new Ic10TokenType("OP_SIN");
  IElementType OP_SLA = new Ic10TokenType("OP_SLA");
  IElementType OP_SLE = new Ic10TokenType("OP_SLE");
  IElementType OP_SLEEP = new Ic10TokenType("OP_SLEEP");
  IElementType OP_SLEZ = new Ic10TokenType("OP_SLEZ");
  IElementType OP_SLL = new Ic10TokenType("OP_SLL");
  IElementType OP_SLT = new Ic10TokenType("OP_SLT");
  IElementType OP_SLTZ = new Ic10TokenType("OP_SLTZ");
  IElementType OP_SNA = new Ic10TokenType("OP_SNA");
  IElementType OP_SNAN = new Ic10TokenType("OP_SNAN");
  IElementType OP_SNANZ = new Ic10TokenType("OP_SNANZ");
  IElementType OP_SNAZ = new Ic10TokenType("OP_SNAZ");
  IElementType OP_SNE = new Ic10TokenType("OP_SNE");
  IElementType OP_SNEZ = new Ic10TokenType("OP_SNEZ");
  IElementType OP_SQRT = new Ic10TokenType("OP_SQRT");
  IElementType OP_SRA = new Ic10TokenType("OP_SRA");
  IElementType OP_SRL = new Ic10TokenType("OP_SRL");
  IElementType OP_SS = new Ic10TokenType("OP_SS");
  IElementType OP_SUB = new Ic10TokenType("OP_SUB");
  IElementType OP_TAN = new Ic10TokenType("OP_TAN");
  IElementType OP_TRUNC = new Ic10TokenType("OP_TRUNC");
  IElementType OP_XOR = new Ic10TokenType("OP_XOR");
  IElementType OP_YIELD = new Ic10TokenType("OP_YIELD");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ALIAS_OP) {
        return new Ic10AliasOpImpl(node);
      }
      else if (type == BIN_OP) {
        return new Ic10BinOpImpl(node);
      }
      else if (type == BIN_OP_NAME) {
        return new Ic10BinOpNameImpl(node);
      }
      else if (type == BRANCH_OP_1) {
        return new Ic10BranchOp1Impl(node);
      }
      else if (type == BRANCH_OP_1_NAME) {
        return new Ic10BranchOp1NameImpl(node);
      }
      else if (type == BRANCH_OP_2) {
        return new Ic10BranchOp2Impl(node);
      }
      else if (type == BRANCH_OP_2_NAME) {
        return new Ic10BranchOp2NameImpl(node);
      }
      else if (type == BRANCH_OP_3) {
        return new Ic10BranchOp3Impl(node);
      }
      else if (type == BRANCH_OP_3_NAME) {
        return new Ic10BranchOp3NameImpl(node);
      }
      else if (type == BRANCH_OP_4) {
        return new Ic10BranchOp4Impl(node);
      }
      else if (type == BRANCH_OP_4_NAME) {
        return new Ic10BranchOp4NameImpl(node);
      }
      else if (type == BRANCH_OP_DEV) {
        return new Ic10BranchOpDevImpl(node);
      }
      else if (type == BRANCH_OP_DEV_NAME) {
        return new Ic10BranchOpDevNameImpl(node);
      }
      else if (type == CLRD_OP) {
        return new Ic10ClrdOpImpl(node);
      }
      else if (type == CLR_OP) {
        return new Ic10ClrOpImpl(node);
      }
      else if (type == DEFINE_OP) {
        return new Ic10DefineOpImpl(node);
      }
      else if (type == DEVICE) {
        return new Ic10DeviceImpl(node);
      }
      else if (type == GET_D_OP) {
        return new Ic10GetDOpImpl(node);
      }
      else if (type == GET_OP) {
        return new Ic10GetOpImpl(node);
      }
      else if (type == HASH) {
        return new Ic10HashImpl(node);
      }
      else if (type == HCF_OP) {
        return new Ic10HcfOpImpl(node);
      }
      else if (type == LABEL) {
        return new Ic10LabelImpl(node);
      }
      else if (type == LBNS_OP) {
        return new Ic10LbnsOpImpl(node);
      }
      else if (type == LBN_OP) {
        return new Ic10LbnOpImpl(node);
      }
      else if (type == LBS_OP) {
        return new Ic10LbsOpImpl(node);
      }
      else if (type == LB_OP) {
        return new Ic10LbOpImpl(node);
      }
      else if (type == LD_OP) {
        return new Ic10LdOpImpl(node);
      }
      else if (type == LINE) {
        return new Ic10LineImpl(node);
      }
      else if (type == LR_OP) {
        return new Ic10LrOpImpl(node);
      }
      else if (type == LS_OP) {
        return new Ic10LsOpImpl(node);
      }
      else if (type == L_OP) {
        return new Ic10LOpImpl(node);
      }
      else if (type == NUMBER) {
        return new Ic10NumberImpl(node);
      }
      else if (type == OPERATION) {
        return new Ic10OperationImpl(node);
      }
      else if (type == PEEK_OP) {
        return new Ic10PeekOpImpl(node);
      }
      else if (type == POKE_OP) {
        return new Ic10PokeOpImpl(node);
      }
      else if (type == POP_OP) {
        return new Ic10PopOpImpl(node);
      }
      else if (type == PUSH_OP) {
        return new Ic10PushOpImpl(node);
      }
      else if (type == PUTD_OP) {
        return new Ic10PutdOpImpl(node);
      }
      else if (type == PUT_OP) {
        return new Ic10PutOpImpl(node);
      }
      else if (type == RAND_OP) {
        return new Ic10RandOpImpl(node);
      }
      else if (type == RMAP_OP) {
        return new Ic10RmapOpImpl(node);
      }
      else if (type == SBN_OP) {
        return new Ic10SbnOpImpl(node);
      }
      else if (type == SBS_OP) {
        return new Ic10SbsOpImpl(node);
      }
      else if (type == SB_OP) {
        return new Ic10SbOpImpl(node);
      }
      else if (type == SDNS_OP) {
        return new Ic10SdnsOpImpl(node);
      }
      else if (type == SDSE_OP) {
        return new Ic10SdseOpImpl(node);
      }
      else if (type == SD_OP) {
        return new Ic10SdOpImpl(node);
      }
      else if (type == SELECT_OP_1) {
        return new Ic10SelectOp1Impl(node);
      }
      else if (type == SELECT_OP_1_NAME) {
        return new Ic10SelectOp1NameImpl(node);
      }
      else if (type == SELECT_OP_2) {
        return new Ic10SelectOp2Impl(node);
      }
      else if (type == SELECT_OP_2_NAME) {
        return new Ic10SelectOp2NameImpl(node);
      }
      else if (type == SELECT_OP_3) {
        return new Ic10SelectOp3Impl(node);
      }
      else if (type == SELECT_OP_3_NAME) {
        return new Ic10SelectOp3NameImpl(node);
      }
      else if (type == SLEEP_OP) {
        return new Ic10SleepOpImpl(node);
      }
      else if (type == SS_OP) {
        return new Ic10SsOpImpl(node);
      }
      else if (type == S_OP) {
        return new Ic10SOpImpl(node);
      }
      else if (type == UN_OP) {
        return new Ic10UnOpImpl(node);
      }
      else if (type == UN_OP_NAME) {
        return new Ic10UnOpNameImpl(node);
      }
      else if (type == VALUE) {
        return new Ic10ValueImpl(node);
      }
      else if (type == VARIABLE) {
        return new Ic10VariableImpl(node);
      }
      else if (type == YIELD_OP) {
        return new Ic10YieldOpImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
