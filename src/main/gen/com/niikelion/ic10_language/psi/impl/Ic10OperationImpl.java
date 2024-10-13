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

public class Ic10OperationImpl extends ASTWrapperPsiElement implements Ic10Operation {

  public Ic10OperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Ic10Visitor visitor) {
    visitor.visitOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Ic10Visitor) accept((Ic10Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Ic10AliasOp getAliasOp() {
    return findChildByClass(Ic10AliasOp.class);
  }

  @Override
  @Nullable
  public Ic10BinOp getBinOp() {
    return findChildByClass(Ic10BinOp.class);
  }

  @Override
  @Nullable
  public Ic10DefineOp getDefineOp() {
    return findChildByClass(Ic10DefineOp.class);
  }

  @Override
  @Nullable
  public Ic10HcfOp getHcfOp() {
    return findChildByClass(Ic10HcfOp.class);
  }

  @Override
  @Nullable
  public Ic10SleepOp getSleepOp() {
    return findChildByClass(Ic10SleepOp.class);
  }

  @Override
  @Nullable
  public Ic10UnOp getUnOp() {
    return findChildByClass(Ic10UnOp.class);
  }

  @Override
  @Nullable
  public Ic10YieldOp getYieldOp() {
    return findChildByClass(Ic10YieldOp.class);
  }

}
