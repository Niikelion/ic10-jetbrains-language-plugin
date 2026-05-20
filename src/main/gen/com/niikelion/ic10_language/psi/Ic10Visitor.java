// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement;
import com.niikelion.ic10_language.psi.elements.Ic10LabelElement;
import com.niikelion.ic10_language.psi.elements.Ic10ReferenceNameElement;

public class Ic10Visitor extends PsiElementVisitor {

  public void visitChannel(@NotNull Ic10Channel o) {
    visitValueLikeElement(o);
  }

  public void visitChannelNumber(@NotNull Ic10ChannelNumber o) {
    visitPsiElement(o);
  }

  public void visitEnum(@NotNull Ic10Enum o) {
    visitValueLikeElement(o);
  }

  public void visitEnumName(@NotNull Ic10EnumName o) {
    visitPsiElement(o);
  }

  public void visitEnumProperty(@NotNull Ic10EnumProperty o) {
    visitPsiElement(o);
  }

  public void visitLabel(@NotNull Ic10Label o) {
    visitLabelElement(o);
  }

  public void visitLabelName(@NotNull Ic10LabelName o) {
    visitPsiElement(o);
  }

  public void visitLine(@NotNull Ic10Line o) {
    visitPsiElement(o);
  }

  public void visitMacro(@NotNull Ic10Macro o) {
    visitValueLikeElement(o);
  }

  public void visitMacroName(@NotNull Ic10MacroName o) {
    visitPsiElement(o);
  }

  public void visitMacroValue(@NotNull Ic10MacroValue o) {
    visitPsiElement(o);
  }

  public void visitNumber(@NotNull Ic10Number o) {
    visitValueLikeElement(o);
  }

  public void visitOperation(@NotNull Ic10Operation o) {
    visitPsiElement(o);
  }

  public void visitOperationName(@NotNull Ic10OperationName o) {
    visitPsiElement(o);
  }

  public void visitReferenceName(@NotNull Ic10ReferenceName o) {
    visitReferenceNameElement(o);
  }

  public void visitValue(@NotNull Ic10Value o) {
    visitPsiElement(o);
  }

  public void visitLabelElement(@NotNull Ic10LabelElement o) {
    visitPsiElement(o);
  }

  public void visitReferenceNameElement(@NotNull Ic10ReferenceNameElement o) {
    visitPsiElement(o);
  }

  public void visitValueLikeElement(@NotNull Ic10ValueLikeElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
