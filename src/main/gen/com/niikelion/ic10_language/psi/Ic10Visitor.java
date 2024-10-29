// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Ic10Visitor extends PsiElementVisitor {

  public void visitChannel(@NotNull Ic10Channel o) {
    visitPsiElement(o);
  }

  public void visitChannelNumber(@NotNull Ic10ChannelNumber o) {
    visitPsiElement(o);
  }

  public void visitConstant(@NotNull Ic10Constant o) {
    visitPsiElement(o);
  }

  public void visitConstantName(@NotNull Ic10ConstantName o) {
    visitPsiElement(o);
  }

  public void visitHash(@NotNull Ic10Hash o) {
    visitPsiElement(o);
  }

  public void visitHashValue(@NotNull Ic10HashValue o) {
    visitPsiElement(o);
  }

  public void visitLabel(@NotNull Ic10Label o) {
    visitNamedElement(o);
  }

  public void visitLabelName(@NotNull Ic10LabelName o) {
    visitPsiElement(o);
  }

  public void visitLine(@NotNull Ic10Line o) {
    visitPsiElement(o);
  }

  public void visitNumber(@NotNull Ic10Number o) {
    visitPsiElement(o);
  }

  public void visitOperation(@NotNull Ic10Operation o) {
    visitPsiElement(o);
  }

  public void visitOperationName(@NotNull Ic10OperationName o) {
    visitPsiElement(o);
  }

  public void visitReferenceName(@NotNull Ic10ReferenceName o) {
    visitNamedElement(o);
  }

  public void visitValue(@NotNull Ic10Value o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull Ic10NamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
