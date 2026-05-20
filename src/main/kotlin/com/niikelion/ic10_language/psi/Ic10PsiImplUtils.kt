package com.niikelion.ic10_language.psi

import com.intellij.psi.util.childrenOfType
import com.niikelion.ic10_language.logic.IUnresolvedValue
import com.niikelion.ic10_language.logic.Instruction
import com.niikelion.ic10_language.logic.Instructions
import com.niikelion.ic10_language.psi.elements.Ic10ValueLikeElement

class Ic10PsiImplUtils {
    companion object {
        @JvmStatic
        fun getDeclaredName(operation: Ic10Operation): String? = getDeclarationToken(operation)?.name

        @JvmStatic
        fun getDeclarationToken(operation: Ic10Operation): Ic10ReferenceName? {
            val opName = operation.operationName.text
            val instruction = Instructions.get(opName)

            if (instruction?.isDeclaration != true) return null
            val value = operation.valueList.firstOrNull() ?: return null
            val reference = value.referenceName ?: return null
            return reference
        }

        @JvmStatic
        fun getInstruction(operation: Ic10Operation): Instruction? =  Instructions.get(operation.operationName.text)

        @JvmStatic
        fun getValue(value: Ic10Value): IUnresolvedValue? =
            value.childrenOfType<Ic10ValueLikeElement>().firstOrNull()?.getValue()
    }
}