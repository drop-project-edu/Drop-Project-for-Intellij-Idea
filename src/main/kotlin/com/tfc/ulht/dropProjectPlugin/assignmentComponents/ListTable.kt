package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.ProjectComponents
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import org.jetbrains.annotations.Nullable
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

                    if (MouseEvent.BUTTON1==e.button && e.clickCount==2 && !e.isConsumed)  {
                        val selectedRow = this@ListTable.getRow(rowAtPoint)
                        val idOfSelected = selectedRow.id_notVisible
                        val nameOfSelected = selectedRow.name

                        if (Globals.selectedAssignmentID!=idOfSelected) {
                            selectedRow.radioButton.isSelected = true
                            Globals.selectedLine?.radioButton?.isSelected=false
                            Globals.selectedLine = selectedRow
                            Globals.selectedAssignmentID = idOfSelected
                            service<ProjectComponents>().setProjectSelectedAssignmentID(idOfSelected)
                            SelectAssignmentNotifier.notify(project,"<html>The assignment <b>$nameOfSelected</b> was selected</html>")
                        }

                    }
                    val columnAtPoint = source.columnAtPoint(e.point)
                    // instructions column = 3 , 4 now update
                    if ( columnAtPoint == 4  && MouseEvent.BUTTON1 == e.button) {
                        AssignmentInstructions(this@ListTable.getRow(rowAtPoint).id_notVisible).showInstructions(project)
                    }
                    super.mousePressed(e)
                }

            }
        })

    }

}
object SelectAssignmentNotifier {
    fun notify(
        @Nullable project: Project?,
        content: String
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Logged Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .notify(project)
    }
}