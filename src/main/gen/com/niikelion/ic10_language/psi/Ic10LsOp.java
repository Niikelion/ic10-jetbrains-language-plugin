// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Ic10LsOp extends PsiElement {

  @NotNull
  Ic10Device getDevice();

  @NotNull
  List<Ic10Value> getValueList();

  @NotNull
  Ic10Variable getVariable();

}
