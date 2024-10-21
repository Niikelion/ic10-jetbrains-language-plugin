package com.niikelion.ic10_language

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import com.niikelion.ic10_language.psi.Ic10Types


class Ic10CompletionContributor: CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            psiElement(Ic10Types.NAME).inside(psiElement(Ic10Types.REFERENCE_NAME)),
            object: CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val symbols = Ic10PsiUtils.findDeclarations(parameters.originalFile)

                    symbols.forEach {
                        result.addElement(LookupElementBuilder.create(it).withIcon(it.getIcon(0)))
                    }

                    val constants = Constants.all

                    constants.forEach {
                        result.addElement(
                            LookupElementBuilder
                                .create(it.name)
                                .withIcon(Ic10Icons.Constant)
                                .appendTailText(it.value?.let { v -> " = $v"} ?: "", true)
                        )
                    }
                }
            }
        )
        extend(
            CompletionType.BASIC,
            psiElement(Ic10Types.NAME).withParent(psiElement(Ic10Types.OPERATION_NAME)),
            object: CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val instructions = Instructions.all

                    instructions.forEach {
                        result.addElement(LookupElementBuilder
                            .create(it.name)
                            .appendTailText(" ${it.arguments.joinToString(" ") { arg -> arg.name }}", true)
                            .withIcon(Ic10Icons.Function)
                        )
                    }
                }
            }
        )
    }
}