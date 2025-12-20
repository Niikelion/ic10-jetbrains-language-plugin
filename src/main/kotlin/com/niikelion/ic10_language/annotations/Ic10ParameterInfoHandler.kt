package com.niikelion.ic10_language.annotations

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.lang.parameterInfo.*
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.util.PsiTreeUtil
import com.niikelion.ic10_language.documentation.ui.grayed
import com.niikelion.ic10_language.instruction
import com.niikelion.ic10_language.logic.Instruction
import com.niikelion.ic10_language.psi.Ic10Operation
import com.niikelion.ic10_language.ui.html.HtmlBuilder
import com.niikelion.ic10_language.ui.html.css

class Ic10ParameterInfoHandler: ParameterInfoHandler<Ic10Operation, Instruction> {
    override fun findElementForParameterInfo(context: CreateParameterInfoContext): Ic10Operation? {
        val call = findFunctionCall(context.file, context.offset) ?: return null
        val instruction = call.instruction ?: return null

        context.itemsToShow = arrayOf(instruction)
        return call
    }

    override fun showParameterInfo(
        element: Ic10Operation,
        context: CreateParameterInfoContext
    ) {
        context.showHint(element, element.textOffset, this)
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): Ic10Operation? {
        return findFunctionCall(context.file, context.offset)
    }

    override fun updateParameterInfo(
        parameterOwner: Ic10Operation,
        context: UpdateParameterInfoContext
    ) {
        val index = ParameterInfoUtils.getCurrentParameterIndex(parameterOwner.node, context.offset, TokenType.WHITE_SPACE) - 1
        context.setCurrentParameter(index)
    }

    override fun updateUI(
        instruction: Instruction?,
        context: ParameterInfoUIContext
    ) {
        if (instruction == null || instruction.arguments.isEmpty()) {
            return context.setupRawUIComponentPresentation(HtmlBuilder.build {
                text(noParamsMessage())
            })
        }

        val activeIndex = context.currentParameterIndex
        context.setupRawUIComponentPresentation(HtmlBuilder.build {
            span {
                separated({ grayed { text(", ") } }) {
                    instruction.arguments.forEachIndexed { index, arg ->
                        if (activeIndex == index) {
                            span {
                                text("${arg.name}(")
                                separated({ grayed { text("|") } }) {
                                    arg.type.typeNames.forEach { span({ css(HtmlBuilder.tokenKeyCss(
                                        DefaultLanguageHighlighterColors.INSTANCE_METHOD)) }) { text(it) } }
                                }
                                text(")")
                            }
                        } else {
                            grayed {
                                text("${arg.name}(${arg.type.typeNames.joinToString("|")})")
                            }
                        }
                    }
                }
            }
        })
    }

    private fun noParamsMessage(): String {
        return CodeInsightBundle.message("parameter.info.no.parameters")
    }

    private fun findFunctionCall(file: PsiFile, offset: Int): Ic10Operation? {
        val e: PsiElement = file.findElementAt(offset) ?: return null
        return PsiTreeUtil.getParentOfType(e, Ic10Operation::class.java)
    }
}