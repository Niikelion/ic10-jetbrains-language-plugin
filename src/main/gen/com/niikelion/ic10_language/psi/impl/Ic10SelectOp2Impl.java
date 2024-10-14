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

public class Ic10SelectOp2Impl extends ASTWrapperPsiElement implements Ic10SelectOp2 {

  public Ic10SelectOp2Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Ic10Visitor visitor) {
    visitor.visitSelectOp2(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Ic10Visitor) accept((Ic10Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Ic10SelectOp2Name getSelectOp2Name() {
    return findNotNullChildByClass(Ic10SelectOp2Name.class);
  }

  @Override
  @NotNull
  public List<Ic10Value> getValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Ic10Value.class);
  }

  @Override
  @NotNull
  public Ic10Variable getVariable() {
    return findNotNullChildByClass(Ic10Variable.class);
  }

}
