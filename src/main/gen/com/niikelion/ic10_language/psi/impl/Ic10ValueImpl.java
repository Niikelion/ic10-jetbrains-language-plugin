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

public class Ic10ValueImpl extends ASTWrapperPsiElement implements Ic10Value {

  public Ic10ValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Ic10Visitor visitor) {
    visitor.visitValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Ic10Visitor) accept((Ic10Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Ic10Hash getHash() {
    return findChildByClass(Ic10Hash.class);
  }

  @Override
  @Nullable
  public Ic10Number getNumber() {
    return findChildByClass(Ic10Number.class);
  }

  @Override
  @Nullable
  public Ic10ReferenceName getReferenceName() {
    return findChildByClass(Ic10ReferenceName.class);
  }

}
