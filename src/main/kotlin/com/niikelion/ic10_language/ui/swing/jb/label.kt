package com.niikelion.ic10_language.ui.swing.jb

import com.intellij.ui.components.JBLabel
import com.niikelion.ic10_language.ui.swing.SwingBuilder

fun SwingBuilder.label(text: String) = element { JBLabel(text) }