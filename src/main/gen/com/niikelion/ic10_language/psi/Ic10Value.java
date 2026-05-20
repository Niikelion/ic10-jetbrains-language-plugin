// This is a generated file. Not intended for manual editing.
package com.niikelion.ic10_language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.niikelion.ic10_language.logic.IUnresolvedValue;

public interface Ic10Value extends PsiElement {

  @Nullable
  Ic10Channel getChannel();

  @Nullable
  Ic10Enum getEnum();

  @Nullable
  Ic10Macro getMacro();

  @Nullable
  Ic10Number getNumber();

  @Nullable
  Ic10ReferenceName getReferenceName();

  @Nullable IUnresolvedValue getValue();

}
