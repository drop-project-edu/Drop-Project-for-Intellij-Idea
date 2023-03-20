package com.tfc.ulht.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBRadioButton
import com.jetbrains.rd.util.use
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.ProjectComponents
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.TableLine
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import data.Assignment
import data.AssignmentInfoResponse
import okhttp3.Request

enum class PanelRoute {
    LOGIN, SEARCH
}

class SearchAssignment(
    private val searchField: SearchTextField,
    private val toolWindow: DropProjectToolWindow,
    private val route: PanelRoute
) :
    DumbAwareAction(
        "Search Assignment", "Search for assignment by ID", AllIcons.Actions.Search
    ) {
    private val REQUEST_URL = "${Globals.REQUEST_URL}/api/student/assignments"
    private val moshi = Moshi.Builder().build()
    private val assignmentJsonAdapter: JsonAdapter<AssignmentInfoResponse> =
        moshi.adapter(AssignmentInfoResponse::class.java)
    private var assignment: Assignment? = null
    private var errorCode: Int? = null

    override fun actionPerformed(e: AnActionEvent) {
        val assignmentAdded = searchAndUpdateAssignmentList()
        if (assignmentAdded != null)
            DefaultNotification.notify(
                e.project, "<html>You've added <b>${assignmentAdded}</b> to your list</html>"
            )
    }

    fun searchAndUpdateAssignmentList(): String? {
        if (searchField.text.isEmpty()) {
            return null
        }
        val request = Request.Builder().url("$REQUEST_URL/${searchField.text.trim()}").build()

        toolWindow.authentication.httpClient.newCall(request).execute().use { response ->
            if (response.code == 200) {
                val assignmentInfoResponse = assignmentJsonAdapter.fromJson(response.body!!.source())!!
                assignment = assignmentInfoResponse.assignment
                errorCode = assignmentInfoResponse.errorCode
            } else {
                errorCode = response.code
            }
        }
        if (assignment != null) {

            val assignmentLineToAdd = listAndSelectAssignment()

            if (assignmentLineToAdd == null) {
                Messages.showMessageDialog(
                    "This Assignment is already in your list", "Duplicate Assignment", Messages.getWarningIcon()
                )
            } else {
                val updateList = toolWindow.tableModel!!.items.toMutableList()
                updateList.add(assignmentLineToAdd)
                toolWindow.tableModel!!.items = updateList
                if (route == PanelRoute.SEARCH)
                    toolWindow.switchToMainToolbar()
                return assignmentLineToAdd.name
            }
        } else {
            if (route == PanelRoute.LOGIN) {
                DefaultNotification.notify(
                    toolWindow.project,
                    "The assignment associated with this project is no longer available"
                )
                toolWindow.globals.selectedAssignmentID = ""
            } else {
                when (errorCode) {
                    404 -> {
                        Messages.showMessageDialog("Assignment not found", "Not Found", Messages.getErrorIcon())
                    }

                    403 -> {
                        Messages.showMessageDialog(
                            "You're not allowed to view this assignment", "Access Denied", Messages.getErrorIcon()
                        )
                    }

                    401 -> {
                        Messages.showMessageDialog(
                            "You're not authorized to access assignments", "Invalid Token", Messages.getErrorIcon()
                        )
                    }
                }
            }


        }
        return null
    }

    private fun listAndSelectAssignment(): TableLine? {

        val line = TableLine()

        line.name = assignment!!.name
        line.language = assignment!!.language
        if (assignment!!.dueDate.isNullOrEmpty()) {
            line.dueDate = "No due date"
        } else {
            line.dueDate = assignment!!.dueDate.toString()
        }
        line.id_notVisible = assignment!!.id
        line.instructions = assignment?.instructions
        line.radioButton = JBRadioButton()
        toolWindow.globals.selectedAssignmentID = line.id_notVisible
        line.radioButton.isSelected = true
        toolWindow.globals.selectedLine = line
        //save project metadata
        ProjectComponents(toolWindow.project).saveProjectComponents(line.id_notVisible)
        //update statusbar
        /*val statusWidget: PluginStatusWidget = WindowManager.getInstance().getStatusBar(toolWindow.project)
            .getWidget(toolWindow.globals.statusWidgetId) as PluginStatusWidget
        statusWidget.selectedAssignmentID = toolWindow.globals.selectedAssignmentID*/
        toolWindow.tableModel?.items?.forEach { it.radioButton.isSelected = false }

        if (toolWindow.tableModel?.items?.contains(line) == true) {
            toolWindow.tableModel?.items?.forEach {
                if (it.id_notVisible == line.id_notVisible) {
                    it.radioButton.isSelected = true
                }
            }
            return null
        }

        return line
    }

}