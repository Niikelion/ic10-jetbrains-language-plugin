package com.niikelion.ic10_language

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.niikelion.ic10_language.psi.*

class Ic10Annotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is Ic10Line -> annotateLine(element, holder)
            is Ic10Operation -> annotateOperation(element, holder)
            is Ic10Label -> annotateLabel(element, holder)
        }
    }

    private fun annotateLine(element: Ic10Line, holder: AnnotationHolder) {
        val lineNumber = Ic10PsiUtils.getLineNumber(element)
        if (lineNumber <= 128) return

        holder
            .newAnnotation(HighlightSeverity.ERROR, "Line $lineNumber exceeds the 128 limit")
            .create()
    }
    private fun annotateOperation(element: Ic10Operation, holder: AnnotationHolder) {
        val instruction = Instructions.get(element.operationName.text)
            ?: return holder.newAnnotation(HighlightSeverity.ERROR, "Unknown instruction").create()

        //TODO: move tooltip to documentation

        holder
            .newAnnotation(HighlightSeverity.INFORMATION, instruction.name)
            .tooltip(instruction.getTooltipText())
            .range(element.operationName)
            .textAttributes(Ic10SyntaxHighlighter.INSTRUCTION)
            .create()

        val values = element.valueList
        values.forEachIndexed { index, value ->
            if (instruction.arguments.size <= index)
                return holder.newAnnotation(HighlightSeverity.ERROR, "${instruction.name} accepts only ${instruction.arguments.size} arguments").range(value).create()
            annotateValue(instruction.arguments[index], value, holder, instruction.isDeclaration && index == 0)
        }
    }
    private fun annotateValue(argument: Instruction.Arg, value: Ic10Value, holder: AnnotationHolder, expectsUnique: Boolean) {
        holder.newAnnotation(HighlightSeverity.INFORMATION, "").tooltip(argument.getTooltipText()).range(value).create()

        val reference = value.referenceName

        if (reference != null)
            return annotateReference(argument, reference, holder, expectsUnique)

        annotateNumericValue(argument, value, holder)
    }
    private fun annotateReference(argument: Instruction.Arg, element: Ic10ReferenceName, holder: AnnotationHolder, expectsUnique: Boolean) {
        when (argument.type) {
            Instruction.ArgType.Property, Instruction.ArgType.SlotProperty -> return holder.newSilentAnnotation(HighlightSeverity.INFORMATION).textAttributes(Ic10SyntaxHighlighter.CONSTANT).range(element).create()
            else -> {
                val name = element.name!!

                val declarations = Ic10PsiUtils.findDeclarations(element.containingFile, name)
                val constant = Constants.get(name)

                if (expectsUnique) {
                    if (declarations.size != 1 || declarations.first() != element)
                        holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate definition of $name").range(element).create()

                    return
                }

                if (!declarations.isEmpty()) return

                if (constant != null) return annotateConstantReference(element, holder)

                if (Ic10PsiUtils.isValidDevice(element) || Ic10PsiUtils.isValidRegister(element)) return

                annotateMissingReference(element, holder)
            }
        }
    }
    private fun annotateNumericValue(argument: Instruction.Arg, value: Ic10Value, holder: AnnotationHolder) {
        if (!argument.type.acceptsValue)
            holder.newAnnotation(HighlightSeverity.ERROR, "Expected ${argument.type.name}, got value").range(value).create()
    }
    private fun annotateConstantReference(element: Ic10ReferenceName, holder: AnnotationHolder) {
        holder
            .newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(Ic10SyntaxHighlighter.CONSTANT)
            .range(element)
            .create()
    }
    private fun annotateMissingReference(element: Ic10ReferenceName, holder: AnnotationHolder) {
        holder.newAnnotation(HighlightSeverity.ERROR, "${element.name} does not exist").range(element).create()
    }
    private fun annotateLabel(element: Ic10Label, holder: AnnotationHolder) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).textAttributes(Ic10SyntaxHighlighter.LABEL).create()
        val declarations = Ic10PsiUtils.findDeclarations(element.containingFile, element.name!!)

        if (declarations.size != 1 || declarations.first() != element)
            holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate definition of ${element.name}").create()
    }
}