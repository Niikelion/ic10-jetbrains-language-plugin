package com.niikelion.ic10_language.simulation

import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class Ic10SimulatorWindowPanel: JPanel(BorderLayout()) {
    init {
        add(JLabel("Ic10 Simulator Window"), BorderLayout.CENTER)
    }
}