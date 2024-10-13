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
  // OP_ADD | OP_SUB | OP_MUL | OP_DIV | OP_MOD | OP_AND | OP_OR | OP_XOR
  public static boolean binOpName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binOpName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BIN_OP_NAME, "<bin op name>");
    r = consumeToken(b, OP_ADD);
    if (!r) r = consumeToken(b, OP_SUB);
    if (!r) r = consumeToken(b, OP_MUL);
    if (!r) r = consumeToken(b, OP_DIV);
    if (!r) r = consumeToken(b, OP_MOD);
    if (!r) r = consumeToken(b, OP_AND);
    if (!r) r = consumeToken(b, OP_OR);
    if (!r) r = consumeToken(b, OP_XOR);
    exit_section_(b, l, m, r, false, null);
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
  // DECIMAL | FLOAT
  public static boolean number(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "number")) return false;
    if (!nextTokenIs(b, "<number>", DECIMAL, FLOAT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NUMBER, "<number>");
    r = consumeToken(b, DECIMAL);
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
  //                 unOp |
  //                 binOp
  public static boolean operation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPERATION, "<operation>");
    r = hcfOp(b, l + 1);
    if (!r) r = yieldOp(b, l + 1);
    if (!r) r = sleepOp(b, l + 1);
    if (!r) r = defineOp(b, l + 1);
    if (!r) r = aliasOp(b, l + 1);
    if (!r) r = unOp(b, l + 1);
    if (!r) r = binOp(b, l + 1);
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
  // unOpName variable value
  public static boolean unOp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unOp")) return false;
    if (!nextTokenIs(b, "<un op>", OP_MOVE, OP_NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UN_OP, "<un op>");
    r = unOpName(b, l + 1);
    r = r && variable(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OP_MOVE | OP_NOT
  public static boolean unOpName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unOpName")) return false;
    if (!nextTokenIs(b, "<un op name>", OP_MOVE, OP_NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UN_OP_NAME, "<un op name>");
    r = consumeToken(b, OP_MOVE);
    if (!r) r = consumeToken(b, OP_NOT);
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
