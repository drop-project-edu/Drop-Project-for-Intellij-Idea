package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.ui.table.TableView
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.ProjectComponents
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.ListSelectionModel


class ListTable(private val toolWindow: DropProjectToolWindow) :
    TableView<TableLine>(toolWindow.tableModel) {


    init {
        getSelectionModel().selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellSelectionEnabled = true
        isStriped = true
        cursor = Cursor(Cursor.HAND_CURSOR)
        autoCreateRowSorter = true
        if (toolWindow.authentication.alreadyLoggedIn) {
            emptyText.text = "No Assignments"

        } else {
            emptyText.text = "Login to see your Assignments"
        }
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val source = e.source as JTable
                val rowAtPoint = source.rowAtPoint(e.point)
                if (rowAtPoint >= 0) {
                    val selectedRow = this@ListTable.getRow(rowAtPoint)
                    if (MouseEvent.BUTTON1 == e.button && e.clickCount == 2 && !e.isConsumed) {
                        val idOfSelected = selectedRow.id_notVisible
                        val nameOfSelected = selectedRow.name

                        if (toolWindow.globals.selectedAssignmentID != idOfSelected) {
                            selectedRow.radioButton.isSelected = true
                            toolWindow.globals.selectedLine?.radioButton?.isSelected = false
                            toolWindow.globals.selectedLine = selectedRow
                            toolWindow.globals.selectedAssignmentID = idOfSelected
                            ProjectComponents(toolWindow.project).saveProjectComponents(idOfSelected)
                            DefaultNotification.notify(
                                toolWindow.project,
                                "<html>The assignment <b>$nameOfSelected</b> was selected</html>"
                            )
                        }

                    }
                    val columnAtPoint = source.columnAtPoint(e.point)
                    // instructions column = 3 , 4 now update
                    if (columnAtPoint == 4 && MouseEvent.BUTTON1 == e.button) {

                        if (selectedRow.instructions != null)
                            AssignmentInstructions(
                                selectedRow.id_notVisible, selectedRow.instructions!!.format,
                                selectedRow.instructions!!.body
                            ).showInstructions(toolWindow.project)

                    }
                    super.mousePressed(e)
                }

            }
        })

    }

}
