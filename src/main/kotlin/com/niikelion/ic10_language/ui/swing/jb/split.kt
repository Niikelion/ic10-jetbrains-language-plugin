package com.niikelion.ic10_language.ui.swing.jb

import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.JBSplitter
import com.intellij.ui.SideBorder
import com.niikelion.ic10_language.ui.swing.Content
import com.niikelion.ic10_language.ui.swing.SwingBuilder

fun SwingBuilder.split(vertically: Boolean, content: Content): JBSplitter {
    val splitter = JBSplitter(vertically)
    val elements = SwingBuilder.build(content)

    elements.elementAtOrNull(0)?.apply { splitter.firstComponent = this } ?: return splitter
    elements.elementAtOrNull(1)?.apply { splitter.secondComponent = this } ?: return splitter

    splitter.secondComponent?.run {
        border = IdeBorderFactory.createBorder(SideBorder.TOP)
    }

    insert(splitter)
    return splitter
}