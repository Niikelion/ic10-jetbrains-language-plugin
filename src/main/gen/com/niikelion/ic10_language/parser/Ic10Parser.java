// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.niikelion.ic10_language.psi.Ic10Types.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Ic10Parser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return ic10File(b, l + 1);
  }

  /* ********************************************************** */
  // OP_ALIAS NAME NAME
  public static boolean aliasOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aliasOp")) return false;
    if (!nextTokenIs(b, OP_ALIAS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, OP_ALIAS, NAME, NAME);
    exit_section_(b, m, ALIAS_OP, r);
    return r;
  }

  /* ********************************************************** */
  // binOpName variable value value
  public static boolean binOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binOp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BIN_OP, "<bin op>");
    r = binOpName(b, l + 1);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_ADD | OP_AND | OP_ATAN2 | OP_DIV | OP_MAX | OP_MIN | OP_MOD | OP_MUL | OP_NOR | OP_OR | OP_SLA | OP_SLL | OP_SRA | OP_SRL | OP_SUB | OP_XOR
  public static boolean binOpName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binOpName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BIN_OP_NAME, "<bin op name>");
    r = consumeToken(b, OP_ADD);
    if (!r) r = consumeToken(b, OP_AND);
    if (!r) r = consumeToken(b, OP_ATAN2);
    if (!r) r = consumeToken(b, OP_DIV);
    if (!r) r = consumeToken(b, OP_MAX);
    if (!r) r = consumeToken(b, OP_MIN);
    if (!r) r = consumeToken(b, OP_MOD);
    if (!r) r = consumeToken(b, OP_MUL);
    if (!r) r = consumeToken(b, OP_NOR);
    if (!r) r = consumeToken(b, OP_OR);
    if (!r) r = consumeToken(b, OP_SLA);
    if (!r) r = consumeToken(b, OP_SLL);
    if (!r) r = consumeToken(b, OP_SRA);
    if (!r) r = consumeToken(b, OP_SRL);
    if (!r) r = consumeToken(b, OP_SUB);
    if (!r) r = consumeToken(b, OP_XOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // branchOp1Name value
  public static boolean branchOp1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_1, "<branch op 1>");
    r = branchOp1Name(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_J | OP_JAL | OP_JR
  public static boolean branchOp1Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp1Name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_1_NAME, "<branch op 1 name>");
    r = consumeToken(b, OP_J);
    if (!r) r = consumeToken(b, OP_JAL);
    if (!r) r = consumeToken(b, OP_JR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // branchOp2Name value value
  public static boolean branchOp2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_2, "<branch op 2>");
    r = branchOp2Name(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_BEQZ | OP_BEQZAL | OP_BGEZ | OP_BGEZAL | OP_BGTZ | OP_BGTZAL | OP_BLEZ | OP_BLEZAL | OP_BLTZ | OP_BLTZAL | OP_BNAN | OP_BNEZ | OP_BNEZAL | OP_BREQZ | OP_BRGEZ | OP_BRGTZ | OP_BRLEZ | OP_BRLTZ | OP_BRNAN | OP_BRNEZ
  public static boolean branchOp2Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp2Name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_2_NAME, "<branch op 2 name>");
    r = consumeToken(b, OP_BEQZ);
    if (!r) r = consumeToken(b, OP_BEQZAL);
    if (!r) r = consumeToken(b, OP_BGEZ);
    if (!r) r = consumeToken(b, OP_BGEZAL);
    if (!r) r = consumeToken(b, OP_BGTZ);
    if (!r) r = consumeToken(b, OP_BGTZAL);
    if (!r) r = consumeToken(b, OP_BLEZ);
    if (!r) r = consumeToken(b, OP_BLEZAL);
    if (!r) r = consumeToken(b, OP_BLTZ);
    if (!r) r = consumeToken(b, OP_BLTZAL);
    if (!r) r = consumeToken(b, OP_BNAN);
    if (!r) r = consumeToken(b, OP_BNEZ);
    if (!r) r = consumeToken(b, OP_BNEZAL);
    if (!r) r = consumeToken(b, OP_BREQZ);
    if (!r) r = consumeToken(b, OP_BRGEZ);
    if (!r) r = consumeToken(b, OP_BRGTZ);
    if (!r) r = consumeToken(b, OP_BRLEZ);
    if (!r) r = consumeToken(b, OP_BRLTZ);
    if (!r) r = consumeToken(b, OP_BRNAN);
    if (!r) r = consumeToken(b, OP_BRNEZ);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // branchOp3Name value value value
  public static boolean branchOp3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_3, "<branch op 3>");
    r = branchOp3Name(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_BAPZ | OP_BAPZAL | OP_BEQ | OP_BEQAL | OP_BGE | OP_BGEAL | OP_BGT | OP_BGTAL | OP_BLE | OP_BLEAL | OP_BLT | OP_BLTAL | OP_BNAZ | OP_BNAZAL | OP_BNE | OP_BNEAL | OP_BRAPZ | OP_BREQ | OP_BRGE | OP_BRGT | OP_BRLE | OP_BRLT | OP_BRNAZ | OP_BRNE
  public static boolean branchOp3Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp3Name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_3_NAME, "<branch op 3 name>");
    r = consumeToken(b, OP_BAPZ);
    if (!r) r = consumeToken(b, OP_BAPZAL);
    if (!r) r = consumeToken(b, OP_BEQ);
    if (!r) r = consumeToken(b, OP_BEQAL);
    if (!r) r = consumeToken(b, OP_BGE);
    if (!r) r = consumeToken(b, OP_BGEAL);
    if (!r) r = consumeToken(b, OP_BGT);
    if (!r) r = consumeToken(b, OP_BGTAL);
    if (!r) r = consumeToken(b, OP_BLE);
    if (!r) r = consumeToken(b, OP_BLEAL);
    if (!r) r = consumeToken(b, OP_BLT);
    if (!r) r = consumeToken(b, OP_BLTAL);
    if (!r) r = consumeToken(b, OP_BNAZ);
    if (!r) r = consumeToken(b, OP_BNAZAL);
    if (!r) r = consumeToken(b, OP_BNE);
    if (!r) r = consumeToken(b, OP_BNEAL);
    if (!r) r = consumeToken(b, OP_BRAPZ);
    if (!r) r = consumeToken(b, OP_BREQ);
    if (!r) r = consumeToken(b, OP_BRGE);
    if (!r) r = consumeToken(b, OP_BRGT);
    if (!r) r = consumeToken(b, OP_BRLE);
    if (!r) r = consumeToken(b, OP_BRLT);
    if (!r) r = consumeToken(b, OP_BRNAZ);
    if (!r) r = consumeToken(b, OP_BRNE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // branchOp4Name value value value value
  public static boolean branchOp4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_4, "<branch op 4>");
    r = branchOp4Name(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_BAP | OP_BAPAL | OP_BNA | OP_BNAAL | OP_BRAP | OP_BRNA
  public static boolean branchOp4Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOp4Name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_4_NAME, "<branch op 4 name>");
    r = consumeToken(b, OP_BAP);
    if (!r) r = consumeToken(b, OP_BAPAL);
    if (!r) r = consumeToken(b, OP_BNA);
    if (!r) r = consumeToken(b, OP_BNAAL);
    if (!r) r = consumeToken(b, OP_BRAP);
    if (!r) r = consumeToken(b, OP_BRNA);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // branchOpDevName device value
  public static boolean branchOpDev(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOpDev")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_DEV, "<branch op dev>");
    r = branchOpDevName(b, l + 1);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_BDNS | OP_BDNSAL | OP_BDSE | OP_BDSEAL | OP_BRDNS | OP_BRDSE
  public static boolean branchOpDevName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "branchOpDevName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRANCH_OP_DEV_NAME, "<branch op dev name>");
    r = consumeToken(b, OP_BDNS);
    if (!r) r = consumeToken(b, OP_BDNSAL);
    if (!r) r = consumeToken(b, OP_BDSE);
    if (!r) r = consumeToken(b, OP_BDSEAL);
    if (!r) r = consumeToken(b, OP_BRDNS);
    if (!r) r = consumeToken(b, OP_BRDSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_CLR device
  public static boolean clrOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "clrOp")) return false;
    if (!nextTokenIs(b, OP_CLR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_CLR);
    r = r && device(b, l + 1);
    exit_section_(b, m, CLR_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_CLRD value
  public static boolean clrdOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "clrdOp")) return false;
    if (!nextTokenIs(b, OP_CLRD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_CLRD);
    r = r && value(b, l + 1);
    exit_section_(b, m, CLRD_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_DEFINE NAME value
  public static boolean defineOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defineOp")) return false;
    if (!nextTokenIs(b, OP_DEFINE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, OP_DEFINE, NAME);
    r = r && value(b, l + 1);
    exit_section_(b, m, DEFINE_OP, r);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean device(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "device")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, DEVICE, r);
    return r;
  }

  /* ********************************************************** */
  // OP_GETD variable value value
  public static boolean getDOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "getDOp")) return false;
    if (!nextTokenIs(b, OP_GETD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_GETD);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, GET_D_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_GET variable device value
  public static boolean getOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "getOp")) return false;
    if (!nextTokenIs(b, OP_GET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_GET);
    r = r && variable(b, l + 1);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, GET_OP, r);
    return r;
  }

  /* ********************************************************** */
  // HASHSTART HASHCONTENT HASHEND
  public static boolean hash(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash")) return false;
    if (!nextTokenIs(b, HASHSTART)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, HASHSTART, HASHCONTENT, HASHEND);
    exit_section_(b, m, HASH, r);
    return r;
  }

  /* ********************************************************** */
  // OP_HCF
  public static boolean hcfOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hcfOp")) return false;
    if (!nextTokenIs(b, OP_HCF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_HCF);
    exit_section_(b, m, HCF_OP, r);
    return r;
  }

  /* ********************************************************** */
  // (line_ (CRLF line_)*)?
  static boolean ic10File(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ic10File")) return false;
    ic10File_0(b, l + 1);
    return true;
  }

  // line_ (CRLF line_)*
  private static boolean ic10File_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ic10File_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = line_(b, l + 1);
    r = r && ic10File_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CRLF line_)*
  private static boolean ic10File_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ic10File_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ic10File_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ic10File_0_1", c)) break;
    }
    return true;
  }

  // CRLF line_
  private static boolean ic10File_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ic10File_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    r = r && line_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // OP_L variable device value
  public static boolean lOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lOp")) return false;
    if (!nextTokenIs(b, OP_L)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_L);
    r = r && variable(b, l + 1);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, L_OP, r);
    return r;
  }

  /* ********************************************************** */
  // NAME COLON
  public static boolean label(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "label")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAME, COLON);
    exit_section_(b, m, LABEL, r);
    return r;
  }

  /* ********************************************************** */
  // OP_LB variable value value value
  public static boolean lbOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lbOp")) return false;
    if (!nextTokenIs(b, OP_LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_LB);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, LB_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_LBN variable value value value
  public static boolean lbnOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lbnOp")) return false;
    if (!nextTokenIs(b, OP_LBN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_LBN);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, LBN_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_LBNS variable value value value value
  public static boolean lbnsOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lbnsOp")) return false;
    if (!nextTokenIs(b, OP_LBNS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_LBNS);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, LBNS_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_LBS variable value value value value
  public static boolean lbsOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lbsOp")) return false;
    if (!nextTokenIs(b, OP_LBS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_LBS);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, LBS_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_LD variable value value
  public static boolean ldOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ldOp")) return false;
    if (!nextTokenIs(b, OP_LD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_LD);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, LD_OP, r);
    return r;
  }

  /* ********************************************************** */
  // label | operation
  public static boolean line(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LINE, "<line>");
    r = label(b, l + 1);
    if (!r) r = operation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // line? COMMENT?
  static boolean line_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = line__0(b, l + 1);
    r = r && line__1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // line?
  private static boolean line__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line__0")) return false;
    line(b, l + 1);
    return true;
  }

  // COMMENT?
  private static boolean line__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line__1")) return false;
    consumeToken(b, COMMENT);
    return true;
  }

  /* ********************************************************** */
  // OP_LR variable device value value
  public static boolean lrOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lrOp")) return false;
    if (!nextTokenIs(b, OP_LR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_LR);
    r = r && variable(b, l + 1);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, LR_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_LS variable device value value
  public static boolean lsOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lsOp")) return false;
    if (!nextTokenIs(b, OP_LS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_LS);
    r = r && variable(b, l + 1);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, LS_OP, r);
    return r;
  }

  /* ********************************************************** */
  // BINARY | DECIMAL | HEXADECIMAL | FLOAT
  public static boolean number(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "number")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NUMBER, "<number>");
    r = consumeToken(b, BINARY);
    if (!r) r = consumeToken(b, DECIMAL);
    if (!r) r = consumeToken(b, HEXADECIMAL);
    if (!r) r = consumeToken(b, FLOAT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // hcfOp |
  //                 yieldOp |
  //                 sleepOp |
  //                 defineOp |
  //                 aliasOp |
  //                 clrOp |
  //                 clrdOp |
  //                 getOp |
  //                 getDOp |
  //                 lOp |
  //                 lbOp |
  //                 lbnOp |
  //                 lbnsOp |
  //                 lbsOp |
  //                 ldOp |
  //                 lrOp |
  //                 lsOp |
  //                 peekOp |
  //                 pokeOp |
  //                 popOp |
  //                 pushOp |
  //                 putOp |
  //                 putdOp |
  //                 randOp |
  //                 rmapOp |
  //                 sbOp |
  //                 sbnOp |
  //                 sbsOp |
  //                 sdOp |
  //                 sOp |
  //                 sdnsOp |
  //                 sdseOp |
  //                 ssOp |
  //                 unOp |
  //                 binOp |
  //                 branchOpDev |
  //                 branchOp4 |
  //                 branchOp3 |
  //                 branchOp2 |
  //                 branchOp1 |
  //                 selectOp3 |
  //                 selectOp2 |
  //                 selectOp1
  public static boolean operation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPERATION, "<operation>");
    r = hcfOp(b, l + 1);
    if (!r) r = yieldOp(b, l + 1);
    if (!r) r = sleepOp(b, l + 1);
    if (!r) r = defineOp(b, l + 1);
    if (!r) r = aliasOp(b, l + 1);
    if (!r) r = clrOp(b, l + 1);
    if (!r) r = clrdOp(b, l + 1);
    if (!r) r = getOp(b, l + 1);
    if (!r) r = getDOp(b, l + 1);
    if (!r) r = lOp(b, l + 1);
    if (!r) r = lbOp(b, l + 1);
    if (!r) r = lbnOp(b, l + 1);
    if (!r) r = lbnsOp(b, l + 1);
    if (!r) r = lbsOp(b, l + 1);
    if (!r) r = ldOp(b, l + 1);
    if (!r) r = lrOp(b, l + 1);
    if (!r) r = lsOp(b, l + 1);
    if (!r) r = peekOp(b, l + 1);
    if (!r) r = pokeOp(b, l + 1);
    if (!r) r = popOp(b, l + 1);
    if (!r) r = pushOp(b, l + 1);
    if (!r) r = putOp(b, l + 1);
    if (!r) r = putdOp(b, l + 1);
    if (!r) r = randOp(b, l + 1);
    if (!r) r = rmapOp(b, l + 1);
    if (!r) r = sbOp(b, l + 1);
    if (!r) r = sbnOp(b, l + 1);
    if (!r) r = sbsOp(b, l + 1);
    if (!r) r = sdOp(b, l + 1);
    if (!r) r = sOp(b, l + 1);
    if (!r) r = sdnsOp(b, l + 1);
    if (!r) r = sdseOp(b, l + 1);
    if (!r) r = ssOp(b, l + 1);
    if (!r) r = unOp(b, l + 1);
    if (!r) r = binOp(b, l + 1);
    if (!r) r = branchOpDev(b, l + 1);
    if (!r) r = branchOp4(b, l + 1);
    if (!r) r = branchOp3(b, l + 1);
    if (!r) r = branchOp2(b, l + 1);
    if (!r) r = branchOp1(b, l + 1);
    if (!r) r = selectOp3(b, l + 1);
    if (!r) r = selectOp2(b, l + 1);
    if (!r) r = selectOp1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_PEEK variable
  public static boolean peekOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "peekOp")) return false;
    if (!nextTokenIs(b, OP_PEEK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_PEEK);
    r = r && variable(b, l + 1);
    exit_section_(b, m, PEEK_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_POKE value value
  public static boolean pokeOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pokeOp")) return false;
    if (!nextTokenIs(b, OP_POKE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_POKE);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, POKE_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_POP variable
  public static boolean popOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "popOp")) return false;
    if (!nextTokenIs(b, OP_POP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_POP);
    r = r && variable(b, l + 1);
    exit_section_(b, m, POP_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_PUSH value
  public static boolean pushOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pushOp")) return false;
    if (!nextTokenIs(b, OP_PUSH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_PUSH);
    r = r && value(b, l + 1);
    exit_section_(b, m, PUSH_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_PUT device value value
  public static boolean putOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "putOp")) return false;
    if (!nextTokenIs(b, OP_PUT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_PUT);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, PUT_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_PUTD value value value
  public static boolean putdOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "putdOp")) return false;
    if (!nextTokenIs(b, OP_PUTD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_PUTD);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, PUTD_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_RAND variable
  public static boolean randOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "randOp")) return false;
    if (!nextTokenIs(b, OP_RAND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_RAND);
    r = r && variable(b, l + 1);
    exit_section_(b, m, RAND_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_RMAP variable device value
  public static boolean rmapOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rmapOp")) return false;
    if (!nextTokenIs(b, OP_RMAP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_RMAP);
    r = r && variable(b, l + 1);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, RMAP_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_S device value value
  public static boolean sOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sOp")) return false;
    if (!nextTokenIs(b, OP_S)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_S);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, S_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_SB value value value
  public static boolean sbOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sbOp")) return false;
    if (!nextTokenIs(b, OP_SB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SB);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, SB_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_SBN value value value value
  public static boolean sbnOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sbnOp")) return false;
    if (!nextTokenIs(b, OP_SBN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SBN);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, SBN_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_SBS value value value value
  public static boolean sbsOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sbsOp")) return false;
    if (!nextTokenIs(b, OP_SBS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SBS);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, SBS_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_SD value value value
  public static boolean sdOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sdOp")) return false;
    if (!nextTokenIs(b, OP_SD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SD);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, SD_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_SDNS variable device
  public static boolean sdnsOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sdnsOp")) return false;
    if (!nextTokenIs(b, OP_SDNS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SDNS);
    r = r && variable(b, l + 1);
    r = r && device(b, l + 1);
    exit_section_(b, m, SDNS_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_SDSE variable device
  public static boolean sdseOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sdseOp")) return false;
    if (!nextTokenIs(b, OP_SDSE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SDSE);
    r = r && variable(b, l + 1);
    r = r && device(b, l + 1);
    exit_section_(b, m, SDSE_OP, r);
    return r;
  }

  /* ********************************************************** */
  // selectOp1Name variable value
  public static boolean selectOp1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectOp1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_OP_1, "<select op 1>");
    r = selectOp1Name(b, l + 1);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_SEQZ | OP_SGEZ | OP_SGTZ | OP_SLEZ | OP_SLTZ | OP_SNAN | OP_SNANZ | OP_SNEZ
  public static boolean selectOp1Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectOp1Name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_OP_1_NAME, "<select op 1 name>");
    r = consumeToken(b, OP_SEQZ);
    if (!r) r = consumeToken(b, OP_SGEZ);
    if (!r) r = consumeToken(b, OP_SGTZ);
    if (!r) r = consumeToken(b, OP_SLEZ);
    if (!r) r = consumeToken(b, OP_SLTZ);
    if (!r) r = consumeToken(b, OP_SNAN);
    if (!r) r = consumeToken(b, OP_SNANZ);
    if (!r) r = consumeToken(b, OP_SNEZ);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // selectOp2Name variable value value
  public static boolean selectOp2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectOp2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_OP_2, "<select op 2>");
    r = selectOp2Name(b, l + 1);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_SAPZ | OP_SEQ | OP_SGE | OP_SGT | OP_SLE | OP_SLT | OP_SNAZ | OP_SNE
  public static boolean selectOp2Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectOp2Name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_OP_2_NAME, "<select op 2 name>");
    r = consumeToken(b, OP_SAPZ);
    if (!r) r = consumeToken(b, OP_SEQ);
    if (!r) r = consumeToken(b, OP_SGE);
    if (!r) r = consumeToken(b, OP_SGT);
    if (!r) r = consumeToken(b, OP_SLE);
    if (!r) r = consumeToken(b, OP_SLT);
    if (!r) r = consumeToken(b, OP_SNAZ);
    if (!r) r = consumeToken(b, OP_SNE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // selectOp3Name variable value value value
  public static boolean selectOp3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectOp3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_OP_3, "<select op 3>");
    r = selectOp3Name(b, l + 1);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_SAP | OP_SELECT | OP_SNA
  public static boolean selectOp3Name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectOp3Name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_OP_3_NAME, "<select op 3 name>");
    r = consumeToken(b, OP_SAP);
    if (!r) r = consumeToken(b, OP_SELECT);
    if (!r) r = consumeToken(b, OP_SNA);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_SLEEP value
  public static boolean sleepOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sleepOp")) return false;
    if (!nextTokenIs(b, OP_SLEEP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SLEEP);
    r = r && value(b, l + 1);
    exit_section_(b, m, SLEEP_OP, r);
    return r;
  }

  /* ********************************************************** */
  // OP_SS device value value value
  public static boolean ssOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ssOp")) return false;
    if (!nextTokenIs(b, OP_SS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_SS);
    r = r && device(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, SS_OP, r);
    return r;
  }

  /* ********************************************************** */
  // unOpName variable value
  public static boolean unOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unOp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UN_OP, "<un op>");
    r = unOpName(b, l + 1);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_ABS | OP_ACOS | OP_ASIN | OP_ATAN | OP_CEIL | OP_COS | OP_EXP | OP_FLOOR | OP_LOG | OP_MOVE | OP_NOT | OP_ROUND | OP_SIN | OP_SQRT | OP_TAN | OP_TRUNC
  public static boolean unOpName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unOpName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UN_OP_NAME, "<un op name>");
    r = consumeToken(b, OP_ABS);
    if (!r) r = consumeToken(b, OP_ACOS);
    if (!r) r = consumeToken(b, OP_ASIN);
    if (!r) r = consumeToken(b, OP_ATAN);
    if (!r) r = consumeToken(b, OP_CEIL);
    if (!r) r = consumeToken(b, OP_COS);
    if (!r) r = consumeToken(b, OP_EXP);
    if (!r) r = consumeToken(b, OP_FLOOR);
    if (!r) r = consumeToken(b, OP_LOG);
    if (!r) r = consumeToken(b, OP_MOVE);
    if (!r) r = consumeToken(b, OP_NOT);
    if (!r) r = consumeToken(b, OP_ROUND);
    if (!r) r = consumeToken(b, OP_SIN);
    if (!r) r = consumeToken(b, OP_SQRT);
    if (!r) r = consumeToken(b, OP_TAN);
    if (!r) r = consumeToken(b, OP_TRUNC);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // variable | number | hash
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE, "<value>");
    r = variable(b, l + 1);
    if (!r) r = number(b, l + 1);
    if (!r) r = hash(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // OP_YIELD
  public static boolean yieldOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "yieldOp")) return false;
    if (!nextTokenIs(b, OP_YIELD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OP_YIELD);
    exit_section_(b, m, YIELD_OP, r);
    return r;
  }

}
