package com.niikelion.ic10_language.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.impl.source.tree.FileElement
import com.niikelion.ic10_language.Ic10Language
import com.niikelion.ic10_language.psi.Ic10Types


class Ic10FormattingModelBuilder: FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val spacingBuilder = SpacingBuilder(formattingContext.codeStyleSettings, Ic10Language.Instance)
            .around(Ic10Types.VALUE).spaces(1)

        val block = Ic10Block(formattingContext.node, Alignment.createAlignment(), spacingBuilder)

        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile,
            block,
            formattingContext.codeStyleSettings
        )
    }
}

class Ic10Block(
    node: ASTNode,
    val operationAlignment: Alignment,
    private val spacingBuilder: SpacingBuilder
): AbstractBlock(
    node,
    Wrap.createWrap(WrapType.NONE, false),
    if (node.elementType == Ic10Types.OPERATION) operationAlignment else null
) {
    override fun buildChildren(): List<Block?> {
        val blocks: MutableList<Block?> = ArrayList()
        var child = myNode.firstChildNode

        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE && child.elementType != Ic10Types.CRLF) {
                val isLine = child.elementType == Ic10Types.LINE
                val target = if (isLine) child.firstChildNode ?: child else child
                val block: Block = Ic10Block(target, operationAlignment, spacingBuilder)
                blocks.add(block)
            }
            child = child.treeNext
        }
        return blocks
    }

    override fun getIndent(): Indent? {
        if (myNode is FileElement) return Indent.getAbsoluteNoneIndent()
        return when(myNode.elementType) {
            Ic10Types.OPERATION -> Indent.getNormalIndent()
            Ic10Types.LABEL -> Indent.getAbsoluteLabelIndent()
            else -> Indent.getNoneIndent()
        }
    }

    override fun getChildIndent(): Indent? = Indent.getNoneIndent()

    override fun getSpacing(
        child1: Block?,
        child2: Block
    ): Spacing? = spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf(): Boolean = myNode.firstChildNode == null
}