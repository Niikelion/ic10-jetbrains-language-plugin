// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Ic10Operation extends PsiElement {

  @Nullable
  Ic10AliasOp getAliasOp();

  @Nullable
  Ic10BinOp getBinOp();

  @Nullable
  Ic10DefineOp getDefineOp();

  @Nullable
  Ic10HcfOp getHcfOp();

  @Nullable
  Ic10SleepOp getSleepOp();

  @Nullable
  Ic10UnOp getUnOp();

  @Nullable
  Ic10YieldOp getYieldOp();

}
