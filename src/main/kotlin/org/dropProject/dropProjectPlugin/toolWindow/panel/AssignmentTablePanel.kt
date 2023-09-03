package org.dropProject.dropProjectPlugin.toolWindow.panel

import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.table.TableView
import org.dropProject.dropProjectPlugin.assignmentComponents.AssignmentTableLine
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class AssignmentTablePanel(resultsTable: TableView<AssignmentTableLine>) :
    NonOpaquePanel() {
    private val resultsTable: TableView<AssignmentTableLine>

    init {
        this.resultsTable = resultsTable
        init()
    }

    private fun init() {
        border = BorderFactory.createEmptyBorder()
        val scrollPanel = JPanel()
        scrollPanel.border = BorderFactory.createEmptyBorder()
        scrollPanel.layout = BorderLayout()
        scrollPanel.add(ScrollPaneFactory.createScrollPane(resultsTable), BorderLayout.CENTER)
        layout = BorderLayout()
        add(scrollPanel, BorderLayout.CENTER)
    }
}