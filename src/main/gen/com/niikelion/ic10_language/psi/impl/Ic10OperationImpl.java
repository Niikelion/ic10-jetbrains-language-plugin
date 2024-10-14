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
  public Ic10BranchOp1 getBranchOp1() {
    return findChildByClass(Ic10BranchOp1.class);
  }

  @Override
  @Nullable
  public Ic10BranchOp2 getBranchOp2() {
    return findChildByClass(Ic10BranchOp2.class);
  }

  @Override
  @Nullable
  public Ic10BranchOp3 getBranchOp3() {
    return findChildByClass(Ic10BranchOp3.class);
  }

  @Override
  @Nullable
  public Ic10BranchOp4 getBranchOp4() {
    return findChildByClass(Ic10BranchOp4.class);
  }

  @Override
  @Nullable
  public Ic10BranchOpDev getBranchOpDev() {
    return findChildByClass(Ic10BranchOpDev.class);
  }

  @Override
  @Nullable
  public Ic10ClrOp getClrOp() {
    return findChildByClass(Ic10ClrOp.class);
  }

  @Override
  @Nullable
  public Ic10ClrdOp getClrdOp() {
    return findChildByClass(Ic10ClrdOp.class);
  }

  @Override
  @Nullable
  public Ic10DefineOp getDefineOp() {
    return findChildByClass(Ic10DefineOp.class);
  }

  @Override
  @Nullable
  public Ic10GetDOp getGetDOp() {
    return findChildByClass(Ic10GetDOp.class);
  }

  @Override
  @Nullable
  public Ic10GetOp getGetOp() {
    return findChildByClass(Ic10GetOp.class);
  }

  @Override
  @Nullable
  public Ic10HcfOp getHcfOp() {
    return findChildByClass(Ic10HcfOp.class);
  }

  @Override
  @Nullable
  public Ic10LOp getLOp() {
    return findChildByClass(Ic10LOp.class);
  }

  @Override
  @Nullable
  public Ic10LbOp getLbOp() {
    return findChildByClass(Ic10LbOp.class);
  }

  @Override
  @Nullable
  public Ic10LbnOp getLbnOp() {
    return findChildByClass(Ic10LbnOp.class);
  }

  @Override
  @Nullable
  public Ic10LbnsOp getLbnsOp() {
    return findChildByClass(Ic10LbnsOp.class);
  }

  @Override
  @Nullable
  public Ic10LbsOp getLbsOp() {
    return findChildByClass(Ic10LbsOp.class);
  }

  @Override
  @Nullable
  public Ic10LdOp getLdOp() {
    return findChildByClass(Ic10LdOp.class);
  }

  @Override
  @Nullable
  public Ic10LrOp getLrOp() {
    return findChildByClass(Ic10LrOp.class);
  }

  @Override
  @Nullable
  public Ic10LsOp getLsOp() {
    return findChildByClass(Ic10LsOp.class);
  }

  @Override
  @Nullable
  public Ic10PeekOp getPeekOp() {
    return findChildByClass(Ic10PeekOp.class);
  }

  @Override
  @Nullable
  public Ic10PokeOp getPokeOp() {
    return findChildByClass(Ic10PokeOp.class);
  }

  @Override
  @Nullable
  public Ic10PopOp getPopOp() {
    return findChildByClass(Ic10PopOp.class);
  }

  @Override
  @Nullable
  public Ic10PushOp getPushOp() {
    return findChildByClass(Ic10PushOp.class);
  }

  @Override
  @Nullable
  public Ic10PutOp getPutOp() {
    return findChildByClass(Ic10PutOp.class);
  }

  @Override
  @Nullable
  public Ic10PutdOp getPutdOp() {
    return findChildByClass(Ic10PutdOp.class);
  }

  @Override
  @Nullable
  public Ic10RandOp getRandOp() {
    return findChildByClass(Ic10RandOp.class);
  }

  @Override
  @Nullable
  public Ic10RmapOp getRmapOp() {
    return findChildByClass(Ic10RmapOp.class);
  }

  @Override
  @Nullable
  public Ic10SOp getSOp() {
    return findChildByClass(Ic10SOp.class);
  }

  @Override
  @Nullable
  public Ic10SbOp getSbOp() {
    return findChildByClass(Ic10SbOp.class);
  }

  @Override
  @Nullable
  public Ic10SbnOp getSbnOp() {
    return findChildByClass(Ic10SbnOp.class);
  }

  @Override
  @Nullable
  public Ic10SbsOp getSbsOp() {
    return findChildByClass(Ic10SbsOp.class);
  }

  @Override
  @Nullable
  public Ic10SdOp getSdOp() {
    return findChildByClass(Ic10SdOp.class);
  }

  @Override
  @Nullable
  public Ic10SdnsOp getSdnsOp() {
    return findChildByClass(Ic10SdnsOp.class);
  }

  @Override
  @Nullable
  public Ic10SdseOp getSdseOp() {
    return findChildByClass(Ic10SdseOp.class);
  }

  @Override
  @Nullable
  public Ic10SelectOp1 getSelectOp1() {
    return findChildByClass(Ic10SelectOp1.class);
  }

  @Override
  @Nullable
  public Ic10SelectOp2 getSelectOp2() {
    return findChildByClass(Ic10SelectOp2.class);
  }

  @Override
  @Nullable
  public Ic10SelectOp3 getSelectOp3() {
    return findChildByClass(Ic10SelectOp3.class);
  }

  @Override
  @Nullable
  public Ic10SleepOp getSleepOp() {
    return findChildByClass(Ic10SleepOp.class);
  }

  @Override
  @Nullable
  public Ic10SsOp getSsOp() {
    return findChildByClass(Ic10SsOp.class);
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
