package com.niikelion.ic10_language.simulation.debug

import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.application
import com.intellij.xdebugger.frame.XFullValueEvaluator
import com.niikelion.ic10_language.utils.toPrettyString
import java.awt.Dimension
import javax.swing.table.AbstractTableModel

class Ic10ArrayTableEvaluator(
    private val title: String,
    private val getData: () -> List<Pair<String, Double>>
) : XFullValueEvaluator("  View") {

    init {
        setShowValuePopup(false)
    }

    override fun startEvaluation(callback: XFullValueEvaluationCallback) {
        callback.evaluated("")
        val data = getData().filter { (name, _) -> name.startsWith("[") }

        application.invokeLater {
            val model = object : AbstractTableModel() {
                override fun getRowCount() = data.size
                override fun getColumnCount() = 2
                override fun getColumnName(col: Int) = if (col == 0) "Index" else "Value"
                override fun getValueAt(row: Int, col: Int): Any =
                    if (col == 0) data[row].first else data[row].second.toPrettyString()
            }

            val table = JBTable(model).apply {
                setShowGrid(true)
                columnModel.getColumn(0).preferredWidth = 60
                columnModel.getColumn(1).preferredWidth = 160
            }

            val scrollPane = JBScrollPane(table).apply {
                preferredSize = Dimension(260, 420)
            }

            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(scrollPane, table)
                .setTitle(title)
                .setResizable(true)
                .setMovable(true)
                .setRequestFocus(true)
                .createPopup()
                .showInFocusCenter()
        }
    }
}
