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
  // referenceName COLON channelNumber
  public static boolean channel(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channel")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = referenceName(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && channelNumber(b, l + 1);
    exit_section_(b, m, CHANNEL, r);
    return r;
  }

  /* ********************************************************** */
  // DECIMAL
  public static boolean channelNumber(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "channelNumber")) return false;
    if (!nextTokenIs(b, DECIMAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DECIMAL);
    exit_section_(b, m, CHANNEL_NUMBER, r);
    return r;
  }

  /* ********************************************************** */
  // constantName DOT constantName
  public static boolean constant(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constant")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = constantName(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && constantName(b, l + 1);
    exit_section_(b, m, CONSTANT, r);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean constantName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constantName")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, CONSTANT_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // line_*
  static boolean ic10File(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ic10File")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ic10File", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // labelName COLON
  public static boolean label(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "label")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = labelName(b, l + 1);
    r = r && consumeToken(b, COLON);
    exit_section_(b, m, LABEL, r);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean labelName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "labelName")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, LABEL_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // label | operation
  public static boolean line(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = label(b, l + 1);
    if (!r) r = operation(b, l + 1);
    exit_section_(b, m, LINE, r);
    return r;
  }

  /* ********************************************************** */
  // line|CRLF|COMMENT
  static boolean line_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_")) return false;
    boolean r;
    r = line(b, l + 1);
    if (!r) r = consumeToken(b, CRLF);
    if (!r) r = consumeToken(b, COMMENT);
    return r;
  }

  /* ********************************************************** */
  // macroName macroValue MACRO_END
  public static boolean macro(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro")) return false;
    if (!nextTokenIs(b, MACRO_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = macroName(b, l + 1);
    r = r && macroValue(b, l + 1);
    r = r && consumeToken(b, MACRO_END);
    exit_section_(b, m, MACRO, r);
    return r;
  }

  /* ********************************************************** */
  // MACRO_START
  public static boolean macroName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macroName")) return false;
    if (!nextTokenIs(b, MACRO_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MACRO_START);
    exit_section_(b, m, MACRO_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // MACRO_CONTENT
  public static boolean macroValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macroValue")) return false;
    if (!nextTokenIs(b, MACRO_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MACRO_CONTENT);
    exit_section_(b, m, MACRO_VALUE, r);
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
  // operationName value*
  public static boolean operation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operation")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = operationName(b, l + 1);
    r = r && operation_1(b, l + 1);
    exit_section_(b, m, OPERATION, r);
    return r;
  }

  // value*
  private static boolean operation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operation_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!value(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "operation_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // NAME
  public static boolean operationName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationName")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, OPERATION_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean referenceName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceName")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, REFERENCE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // channel | macro | number | constant | referenceName
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE, "<value>");
    r = channel(b, l + 1);
    if (!r) r = macro(b, l + 1);
    if (!r) r = number(b, l + 1);
    if (!r) r = constant(b, l + 1);
    if (!r) r = referenceName(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
