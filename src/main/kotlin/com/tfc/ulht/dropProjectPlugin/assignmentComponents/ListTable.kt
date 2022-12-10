package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import com.tfc.ulht.dropProjectPlugin.Globals
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
                        val idOfSelected = this@ListTable.getRow(rowAtPoint).id_notVisible
                        val nameOfSelected = this@ListTable.getRow(rowAtPoint).name
                        if (Globals.selectedAssignmentID!=idOfSelected) {

                            Globals.selectedAssignmentID = idOfSelected
                            SelectAssignmentNotifier.notify(project,"<html>The Assignment <b>$nameOfSelected</b> Was Selected</html>")
                        }

                    }
                    val columnAtPoint = source.columnAtPoint(e.point)
                    // instructions column = 3
                    if ( columnAtPoint == 3  && MouseEvent.BUTTON1 == e.button) {
                        AssignmentInstructions(this@ListTable.getRow(rowAtPoint).id_notVisible).showInstructions()
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