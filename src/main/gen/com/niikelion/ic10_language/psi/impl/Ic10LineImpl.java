// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.niikelion.ic10_language.psi.Ic10Types.*;
import com.niikelion.ic10_language.psi.*;

public class Ic10LineImpl extends Ic10NamedElementImpl implements Ic10Line {

  public Ic10LineImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Ic10Visitor visitor) {
    visitor.visitLine(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Ic10Visitor) accept((Ic10Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Ic10Label getLabel() {
    return findChildByClass(Ic10Label.class);
  }

  @Override
  @Nullable
  public Ic10Operation getOperation() {
    return findChildByClass(Ic10Operation.class);
  }

}
