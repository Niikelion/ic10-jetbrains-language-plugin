package com.niikelion.ic10_language.logic

import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.util.elementType
import com.niikelion.ic10_language.logic.ProgramCode.Line
import com.niikelion.ic10_language.psi.Ic10File
import com.niikelion.ic10_language.psi.Ic10Line
import com.niikelion.ic10_language.psi.Ic10Types
import com.niikelion.ic10_language.psi.Ic10Value
import com.niikelion.ic10_language.utils.splitWhen

private val noop = Instruction("", "Does nothing", listOf(), false) {}
private val emptyLine = Line(noop, emptyArray())

class ProgramCode(val lines: Array<Line>, val source: Ic10File) {
    class Line(val instruction: Instruction, val args: Array<IUnresolvedValue>, val sourceLine: Int = -1)

    companion object {
        @Throws(CompilationError::class)
        fun compile(file: Ic10File): ProgramCode {
            val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
            val lineGroups = file.children.filter { it.elementType != TokenType.WHITE_SPACE }.splitWhen { it.elementType == Ic10Types.CRLF }
            val compiledLines = lineGroups.map { group ->
                val line = compileLine(group)
                if (document == null) line
                else {
                    val sourceLine = group.firstOrNull { it.elementType == Ic10Types.LINE }
                        ?.let { document.getLineNumber(it.textOffset) }
                        ?: -1
                    Line(line.instruction, line.args, sourceLine)
                }
            }.toTypedArray()
            return ProgramCode(compiledLines, file)
        }
        @Throws(CompilationError::class)
        fun compileLine(line: List<PsiElement>): Line {
            val instructionElement = line.firstOrNull { it.elementType == Ic10Types.LINE }
            if (instructionElement !is Ic10Line) return emptyLine

            val operation = instructionElement.operation ?: return emptyLine
            val instruction = operation.instruction ?: throw CompilationError("Unknown operation ${operation.operationName.text}")
            if (instruction.action == null) throw CompilationError("Instruction ${instruction.name} is not implemented yet")
            val args = operation.valueList.map(this::compileValue)

            return Line(instruction, args.toTypedArray())
        }
        @Throws(CompilationError::class)
        fun compileValue(value: Ic10Value): IUnresolvedValue {
            return value.value ?: throw CompilationError("Could not compile ${value.text}", value)
        }
    }
}

class CompilationError(message: String, val target: PsiElement? = null): Exception(message)