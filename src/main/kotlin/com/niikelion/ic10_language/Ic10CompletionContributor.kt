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
            psiElement(Ic10Types.NAME),
            object: CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    result.addElement(LookupElementBuilder.create("test"))
                }
            }
        )
    }
}