// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.niikelion.ic10_language.psi.Ic10Types.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.niikelion.ic10_language.psi.*;

public class Ic10BranchOp1Impl extends ASTWrapperPsiElement implements Ic10BranchOp1 {

  public Ic10BranchOp1Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Ic10Visitor visitor) {
    visitor.visitBranchOp1(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Ic10Visitor) accept((Ic10Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Ic10BranchOp1Name getBranchOp1Name() {
    return findNotNullChildByClass(Ic10BranchOp1Name.class);
  }

  @Override
  @NotNull
  public Ic10JumpTarget getJumpTarget() {
    return findNotNullChildByClass(Ic10JumpTarget.class);
  }

}
