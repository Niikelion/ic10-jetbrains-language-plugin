package com.niikelion.ic10_language.ui.swing

import java.awt.Dimension
import javax.swing.JComponent

fun JComponent.fillWidth() {
    preferredSize = Dimension(maximumSize.width, preferredSize.height)
}
fun JComponent.fillHeight() {
    preferredSize = Dimension(preferredSize.width, maximumSize.height)
}
fun JComponent.fillParent() {
    preferredSize = maximumSize
}

fun JComponent.maxWidth(width: Int) {
    maximumSize = Dimension(width, maximumSize.height)
}
fun JComponent.maxHeight(height: Int) {
    maximumSize = Dimension(maximumSize.width, height)
}
fun JComponent.minSize(width: Int, height: Int) {
    maximumSize = Dimension(width, height)
}