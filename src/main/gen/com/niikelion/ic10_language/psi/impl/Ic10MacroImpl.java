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

public class Ic10MacroImpl extends ASTWrapperPsiElement implements Ic10Macro {

  public Ic10MacroImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Ic10Visitor visitor) {
    visitor.visitMacro(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Ic10Visitor) accept((Ic10Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Ic10MacroName getMacroName() {
    return findNotNullChildByClass(Ic10MacroName.class);
  }

  @Override
  @NotNull
  public Ic10MacroValue getMacroValue() {
    return findNotNullChildByClass(Ic10MacroValue.class);
  }

}
