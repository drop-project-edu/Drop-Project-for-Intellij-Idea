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
    TableView<AssignmentTableLine>(toolWindow.tableModel) {


    init {
        getSelectionModel().selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellSelectionEnabled = true
        isStriped = true
        cursor = Cursor(Cursor.HAND_CURSOR)
        autoCreateRowSorter = true
        //RADIO BUTTON RESTRICT COLUMN WIDTH
        columnModel.getColumn(0).maxWidth = 40
        columnModel.getColumn(0).minWidth = 20
        //ASSIGNMENT NAME COLLUM WIDTH
        columnModel.getColumn(1).preferredWidth = 300
        //ASSIGNMENT LANGUAGE COLLUM WIDTH
        columnModel.getColumn(2).preferredWidth = 50
        //ASSIGNMENT DUE DATE COLLUM WIDTH
        columnModel.getColumn(3).preferredWidth = 150
        //DETAILS BUTTON RESTRICT COLUMN WIDTH
        columnModel.getColumn(4).maxWidth = 180
        columnModel.getColumn(4).preferredWidth = 170

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
                                toolWindow.globals.REQUEST_URL,
                                selectedRow.id_notVisible, selectedRow.instructions!!.format,
                                selectedRow.instructions!!.body
                            ).showInstructions(toolWindow.project)

                    }
                    if (MouseEvent.BUTTON3 == e.button) {
                        println("RIGHT MOUSE CLICK")
                    }
                    super.mousePressed(e)
                }

            }
        })

    }

}
