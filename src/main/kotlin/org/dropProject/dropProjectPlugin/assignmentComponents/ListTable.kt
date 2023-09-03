package org.dropProject.dropProjectPlugin.assignmentComponents

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.table.TableView
import org.dropProject.dropProjectPlugin.DefaultNotification
import org.dropProject.dropProjectPlugin.ProjectComponents
import org.dropProject.dropProjectPlugin.actions.ForgetAssignment
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.ListSelectionModel


class ListTable(private val toolWindow: DropProjectToolWindow) : TableView<AssignmentTableLine>(toolWindow.tableModel) {


    init {
        getSelectionModel().selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellSelectionEnabled = true
        isStriped = true
        cursor = Cursor(Cursor.HAND_CURSOR)
        autoCreateRowSorter = true
        //RADIO BUTTON RESTRICT COLUMN WIDTH
        columnModel.getColumn(0).maxWidth = 30
        columnModel.getColumn(0).minWidth = 20
        //RADIO BUTTON CENTER CONTENT
        //ASSIGNMENT NAME COLLUM WIDTH
        columnModel.getColumn(1).preferredWidth = 300
        //ASSIGNMENT LANGUAGE COLLUM WIDTH
        columnModel.getColumn(2).preferredWidth = 90
        //ASSIGNMENT DUE DATE COLLUM WIDTH
        columnModel.getColumn(3).preferredWidth = 140
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

                        if (toolWindow.globals.selectedLine != selectedRow) {
                            selectedRow.radioButton.isSelected = true
                            toolWindow.globals.selectedLine?.radioButton?.isSelected = false
                            toolWindow.globals.selectedLine = selectedRow
                            //toolWindow.globals.selectedAssignmentID = idOfSelected
                            ProjectComponents(toolWindow.project).saveProjectComponents(idOfSelected)
                            DefaultNotification.notify(
                                toolWindow.project, "<html>The assignment <b>$nameOfSelected</b> was selected</html>"
                            )
                        }

                    }
                    val columnAtPoint = source.columnAtPoint(e.point)
                    //details
                    if (columnAtPoint == 4 && MouseEvent.BUTTON1 == e.button) {

                        if (selectedRow.instructions != null) AssignmentInstructions(
                            toolWindow.globals.REQUEST_URL,
                            selectedRow.id_notVisible,
                            selectedRow.instructions!!.format,
                            selectedRow.instructions!!.body
                        ).showInstructions(toolWindow.project)

                    }
                    if (MouseEvent.BUTTON3 == e.button) {
                        //POPUP DIALOG
                        val builder = JBPopupFactory.getInstance().createActionGroupPopup(
                            null,
                            DefaultActionGroup(
                                ForgetAssignment(selectedRow, toolWindow)
                            ),
                            DataManager.getInstance().getDataContext(e.component),
                            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                            true
                        )
                        builder.setMinimumSize(Dimension(110, 35))
                        val mousePosition = e.component.mousePosition ?: Point(0, 0)
                        builder.show(RelativePoint(e.component, mousePosition))

                        super.mousePressed(e)
                    }
                }

            }
        })

    }

}
