// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.niikelion.ic10_language.psi.impl.*;

public interface Ic10Types {

  IElementType CHANNEL = new Ic10ElementType("CHANNEL");
  IElementType CHANNEL_NUMBER = new Ic10ElementType("CHANNEL_NUMBER");
  IElementType CONSTANT = new Ic10ElementType("CONSTANT");
  IElementType CONSTANT_NAME = new Ic10ElementType("CONSTANT_NAME");
  IElementType HASH = new Ic10ElementType("HASH");
  IElementType HASH_VALUE = new Ic10ElementType("HASH_VALUE");
  IElementType LABEL = new Ic10ElementType("LABEL");
  IElementType LABEL_NAME = new Ic10ElementType("LABEL_NAME");
  IElementType LINE = new Ic10ElementType("LINE");
  IElementType NUMBER = new Ic10ElementType("NUMBER");
  IElementType OPERATION = new Ic10ElementType("OPERATION");
  IElementType OPERATION_NAME = new Ic10ElementType("OPERATION_NAME");
  IElementType REFERENCE_NAME = new Ic10ElementType("REFERENCE_NAME");
  IElementType VALUE = new Ic10ElementType("VALUE");

  IElementType BINARY = new Ic10TokenType("BINARY");
  IElementType CLOSEBRACKET = new Ic10TokenType("CLOSEBRACKET");
  IElementType COLON = new Ic10TokenType("COLON");
  IElementType COMMENT = new Ic10TokenType("COMMENT");
  IElementType CRLF = new Ic10TokenType("CRLF");
  IElementType DECIMAL = new Ic10TokenType("DECIMAL");
  IElementType DOT = new Ic10TokenType("DOT");
  IElementType FLOAT = new Ic10TokenType("FLOAT");
  IElementType HASHCONTENT = new Ic10TokenType("HASHCONTENT");
  IElementType HEXADECIMAL = new Ic10TokenType("HEXADECIMAL");
  IElementType NAME = new Ic10TokenType("NAME");
  IElementType OPENBRACKET = new Ic10TokenType("OPENBRACKET");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == CHANNEL) {
        return new Ic10ChannelImpl(node);
      }
      else if (type == CHANNEL_NUMBER) {
        return new Ic10ChannelNumberImpl(node);
      }
      else if (type == CONSTANT) {
        return new Ic10ConstantImpl(node);
      }
      else if (type == CONSTANT_NAME) {
        return new Ic10ConstantNameImpl(node);
      }
      else if (type == HASH) {
        return new Ic10HashImpl(node);
      }
      else if (type == HASH_VALUE) {
        return new Ic10HashValueImpl(node);
      }
      else if (type == LABEL) {
        return new Ic10LabelImpl(node);
      }
      else if (type == LABEL_NAME) {
        return new Ic10LabelNameImpl(node);
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
      else if (type == OPERATION_NAME) {
        return new Ic10OperationNameImpl(node);
      }
      else if (type == REFERENCE_NAME) {
        return new Ic10ReferenceNameImpl(node);
      }
      else if (type == VALUE) {
        return new Ic10ValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
