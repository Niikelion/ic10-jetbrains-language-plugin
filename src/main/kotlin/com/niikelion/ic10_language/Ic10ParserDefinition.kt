package com.niikelion.ic10_language

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.niikelion.ic10_language.parser.Ic10Parser
import com.niikelion.ic10_language.psi.Ic10File
import com.niikelion.ic10_language.psi.Ic10TokenSets
import com.niikelion.ic10_language.psi.Ic10Types

class Ic10ParserDefinition: ParserDefinition {
    companion object {
        val File: IFileElementType = IFileElementType(Ic10Language.Instance)
    }

    override fun createLexer(project: Project?): Lexer = Ic10LexerAdapter()

    override fun createParser(project: Project?): PsiParser = Ic10Parser()

    override fun getFileNodeType(): IFileElementType = File

    override fun getCommentTokens(): TokenSet = Ic10TokenSets.COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode?): PsiElement = Ic10Types.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = Ic10File(viewProvider)
}