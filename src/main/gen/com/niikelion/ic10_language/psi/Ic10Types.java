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
  IElementType DEFINE_OP = new Ic10ElementType("DEFINE_OP");
  IElementType HASH = new Ic10ElementType("HASH");
  IElementType HCF_OP = new Ic10ElementType("HCF_OP");
  IElementType LABEL = new Ic10ElementType("LABEL");
  IElementType LINE = new Ic10ElementType("LINE");
  IElementType NUMBER = new Ic10ElementType("NUMBER");
  IElementType OPERATION = new Ic10ElementType("OPERATION");
  IElementType SLEEP_OP = new Ic10ElementType("SLEEP_OP");
  IElementType UN_OP = new Ic10ElementType("UN_OP");
  IElementType UN_OP_NAME = new Ic10ElementType("UN_OP_NAME");
  IElementType VALUE = new Ic10ElementType("VALUE");
  IElementType VARIABLE = new Ic10ElementType("VARIABLE");
  IElementType YIELD_OP = new Ic10ElementType("YIELD_OP");

  IElementType COLON = new Ic10TokenType("COLON");
  IElementType COMMENT = new Ic10TokenType("COMMENT");
  IElementType CRLF = new Ic10TokenType("CRLF");
  IElementType DECIMAL = new Ic10TokenType("DECIMAL");
  IElementType FLOAT = new Ic10TokenType("FLOAT");
  IElementType HASHCONTENT = new Ic10TokenType("HASHCONTENT");
  IElementType HASHEND = new Ic10TokenType("HASHEND");
  IElementType HASHSTART = new Ic10TokenType("HASHSTART");
  IElementType NAME = new Ic10TokenType("NAME");
  IElementType OP_ADD = new Ic10TokenType("OP_ADD");
  IElementType OP_ALIAS = new Ic10TokenType("OP_ALIAS");
  IElementType OP_AND = new Ic10TokenType("OP_AND");
  IElementType OP_DEFINE = new Ic10TokenType("OP_DEFINE");
  IElementType OP_DIV = new Ic10TokenType("OP_DIV");
  IElementType OP_HCF = new Ic10TokenType("OP_HCF");
  IElementType OP_MOD = new Ic10TokenType("OP_MOD");
  IElementType OP_MOVE = new Ic10TokenType("OP_MOVE");
  IElementType OP_MUL = new Ic10TokenType("OP_MUL");
  IElementType OP_NOT = new Ic10TokenType("OP_NOT");
  IElementType OP_OR = new Ic10TokenType("OP_OR");
  IElementType OP_SLEEP = new Ic10TokenType("OP_SLEEP");
  IElementType OP_SUB = new Ic10TokenType("OP_SUB");
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
      else if (type == DEFINE_OP) {
        return new Ic10DefineOpImpl(node);
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
      else if (type == LINE) {
        return new Ic10LineImpl(node);
      }
      else if (type == NUMBER) {
        return new Ic10NumberImpl(node);
      }
      else if (type == OPERATION) {
        return new Ic10OperationImpl(node);
      }
      else if (type == SLEEP_OP) {
        return new Ic10SleepOpImpl(node);
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
