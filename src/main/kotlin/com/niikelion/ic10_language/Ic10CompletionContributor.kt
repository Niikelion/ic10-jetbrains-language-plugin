package com.niikelion.ic10_language

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.util.findParentOfType
import com.intellij.util.ProcessingContext
import com.niikelion.ic10_language.logic.Constants
import com.niikelion.ic10_language.logic.Enums
import com.niikelion.ic10_language.logic.Instructions
import com.niikelion.ic10_language.psi.Ic10Enum
import com.niikelion.ic10_language.psi.Ic10Types
import com.niikelion.ic10_language.utils.toPrettyString


class Ic10CompletionContributor: CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            psiElement(Ic10Types.NAME).inside(psiElement(Ic10Types.REFERENCE_NAME)),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    Ic10PsiUtils.findDeclarations(parameters.originalFile).forEach {
                        result.addElement(
                            LookupElementBuilder
                                .create(it)
                                .withIcon(it.getIcon(0))
                        )
                    }
                    Enums.allEnums.forEach {
                        result.addElement(
                            LookupElementBuilder
                                .create("${it.name}.")
                                .withIcon(Ic10Icons.Enum)
                        )
                    }
                    Constants.all.values.forEach {
                        result.addElement(
                            LookupElementBuilder
                                .create(it.name)
                                .appendTailText(" = ${it.value.toPrettyString()}", true)
                                .withIcon(Ic10Icons.Constant)
                        )
                    }
                }
            }
        )
        extend(
            CompletionType.BASIC,
            psiElement(Ic10Types.NAME).withParent(psiElement(Ic10Types.OPERATION_NAME)),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    Instructions.all.forEach {
                        result.addElement(
                            LookupElementBuilder
                                .create(it.name)
                                .appendTailText(" ${it.arguments.joinToString(" ") { arg -> arg.name }}", true)
                                .withIcon(Ic10Icons.Function)
                        )
                    }
                }
            }
        )
        extend(
            CompletionType.BASIC,
            psiElement(Ic10Types.NAME).withParent(psiElement(Ic10Types.ENUM_PROPERTY)),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val enumElement = parameters.position.findParentOfType<Ic10Enum>() ?: return
                    val enum = Enums.get(enumElement.enumName.text) ?: return
                    enum.values.forEach {
                        result.addElement(
                            LookupElementBuilder
                                .create(it.key)
                                .appendTailText(" = ${it.value.value.toPrettyString()}", true)
                                .withIcon(Ic10Icons.Field)
                                .withStrikeoutness(it.value.deprecated)
                        )
                    }
                }
            }
        )
    }
}