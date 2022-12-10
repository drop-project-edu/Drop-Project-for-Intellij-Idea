package com.tfc.ulht.dropProjectPlugin.toolWindow

import AssignmentTableModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SideBorder
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.ListTable
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.AssignmentTablePanel
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarPanel
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel

class DropProjectToolWindow(project: Project) {

    private var contentToolWindow: JPanel? = null

    companion object{
        lateinit var tableModel:AssignmentTableModel
        lateinit var resultsTable : ListTable

    }

    fun getContent(): JComponent? {
        return contentToolWindow
    }

    init {
        contentToolWindow = SimpleToolWindowPanel(true,true)

        tableModel = AssignmentTableModel(AssignmentTableModel.generateColumnInfo(), ArrayList())
        resultsTable = ListTable(tableModel,project)
        val assignmentTablePanel = AssignmentTablePanel(resultsTable)
        assignmentTablePanel.border = IdeBorderFactory.createBorder(SideBorder.TOP or SideBorder.RIGHT)

        val toolbarPanel = ToolbarPanel()
        toolbarPanel.border = IdeBorderFactory.createBorder(SideBorder.TOP or SideBorder.RIGHT or SideBorder.BOTTOM)
        val horizontalSplitter = OnePixelSplitter(true,0.0f)
        horizontalSplitter.border = BorderFactory.createEmptyBorder()
        horizontalSplitter.dividerPositionStrategy = Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE
        horizontalSplitter.setResizeEnabled(false)
        horizontalSplitter.firstComponent = toolbarPanel
        horizontalSplitter.secondComponent = assignmentTablePanel
        (this.contentToolWindow as SimpleToolWindowPanel).add(horizontalSplitter)

    }

}