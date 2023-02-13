package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.openapi.project.Project
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.ProjectComponents
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.ListSelectionModel


class ListTable(model: ListTableModel<TableLine>?,project: Project?) : TableView<TableLine>(model) {


    init {
        getSelectionModel().selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellSelectionEnabled = true
        isStriped = true
        cursor = Cursor(Cursor.HAND_CURSOR)
        autoCreateRowSorter = true
        if (Authentication.alreadyLoggedIn){
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
                    if (MouseEvent.BUTTON1==e.button && e.clickCount==2 && !e.isConsumed)  {
                        val idOfSelected = selectedRow.id_notVisible
                        val nameOfSelected = selectedRow.name

                        if (Globals.selectedAssignmentID!=idOfSelected) {
                            selectedRow.radioButton.isSelected = true
                            Globals.selectedLine?.radioButton?.isSelected=false
                            Globals.selectedLine = selectedRow
                            Globals.selectedAssignmentID = idOfSelected
                            /*service<ProjectComponents>().setProjectSelectedAssignmentID(idOfSelected)
                            Keeps data between overall plugin sessions*/
                            ProjectComponents().saveProjectComponents(idOfSelected)
                           DefaultNotification.notify(project,
                                "<html>The assignment <b>$nameOfSelected</b> was selected</html>")
                        }

                    }
                    val columnAtPoint = source.columnAtPoint(e.point)
                    // instructions column = 3 , 4 now update
                    if ( columnAtPoint == 4  && MouseEvent.BUTTON1 == e.button) {

                        if (selectedRow.instructions!= null)
                        AssignmentInstructions(selectedRow.id_notVisible, selectedRow.instructions!!.format,
                            selectedRow.instructions!!.body).showInstructions(project)

                    }
                    super.mousePressed(e)
                }

            }
        })

    }

}
