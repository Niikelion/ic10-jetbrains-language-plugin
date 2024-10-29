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

public class Ic10ConstantImpl extends ASTWrapperPsiElement implements Ic10Constant {

  public Ic10ConstantImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Ic10Visitor visitor) {
    visitor.visitConstant(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Ic10Visitor) accept((Ic10Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Ic10ConstantName> getConstantNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Ic10ConstantName.class);
  }

}
