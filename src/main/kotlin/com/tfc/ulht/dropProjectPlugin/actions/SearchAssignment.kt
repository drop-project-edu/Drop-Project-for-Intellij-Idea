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
import com.tfc.ulht.dropProjectPlugin.ProjectComponents
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.AssignmentTableLine
import com.tfc.ulht.dropProjectPlugin.settings.SettingsState
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import data.Assignment
import data.AssignmentInfoResponse
import okhttp3.Request

enum class PanelRoute {
    LOGIN, SEARCH
}

class SearchAssignment(
    private val assignmentIDField: SearchTextField? = null,
    private var assignmentID: String? = null,
    private val toolWindow: DropProjectToolWindow,
    private val route: PanelRoute,
    private val selectAssignment: Boolean
) :
    DumbAwareAction(
        "Search Assignment", "Search for assignment by ID", AllIcons.Actions.Search
    ) {
    private val REQUEST_URL: String
        get() {
            return "${toolWindow.globals.REQUEST_URL}/api/student/assignments"
        }
    private val moshi = Moshi.Builder().build()
    private val assignmentJsonAdapter: JsonAdapter<AssignmentInfoResponse> =
        moshi.adapter(AssignmentInfoResponse::class.java)
    private var assignment: Assignment? = null
    private var errorCode: Int? = null

    override fun actionPerformed(e: AnActionEvent) {
        if (assignmentIDField != null && assignmentID == null) {
            assignmentID = assignmentIDField.text
        }
        val assignmentAdded = searchAndUpdateAssignmentList()
        if (assignmentAdded != null)
            DefaultNotification.notify(
                e.project, "<html>You've added <b>${assignmentAdded}</b> to your list</html>"
            )
    }

    private fun searchAssignment() {
        assignment = null
        val request = Request.Builder().url("$REQUEST_URL/${assignmentID!!.trim()}").build()

        toolWindow.authentication.httpClient.newCall(request).execute().use { response ->
            if (response.code == 200) {
                val assignmentInfoResponse = assignmentJsonAdapter.fromJson(response.body!!.source())!!
                assignment = assignmentInfoResponse.assignment
                errorCode = assignmentInfoResponse.errorCode
            } else {
                errorCode = response.code
            }
        }
    }

    fun searchAndUpdateAssignmentList(): String? {
        if (assignmentID!!.isEmpty()) {
            return null
        }
        searchAssignment()

        if (assignment != null) {

            var assignmentLineToAdd = formatAssignmentToTableLine()

            if (toolWindow.tableModel?.items?.indexOf(assignmentLineToAdd) != -1) {
                Messages.showMessageDialog(
                    "This Assignment is already in your list", "Duplicate Assignment", Messages.getWarningIcon()
                )
            } else {
                if (selectAssignment) {
                    assignmentLineToAdd = selectAssignment(assignmentLineToAdd)
                }
                val updateList = toolWindow.tableModel!!.items.toMutableList()
                updateList.add(assignmentLineToAdd)
                toolWindow.tableModel!!.items = updateList
                if (route == PanelRoute.SEARCH) {
                    toolWindow.switchToMainToolbar()
                    //ADD TO SETTINGS STATE CLASS, KEEPS BETWEEN RUNS
                    SettingsState.getInstance().addPublicAssignment(assignmentLineToAdd.id_notVisible)
                }
                return assignmentLineToAdd.name
            }
        } else {
            if (route == PanelRoute.LOGIN) {
                DefaultNotification.notify(
                    toolWindow.project,
                    "<html>The assignment <b>$assignmentID</b> is no longer available</html>"
                )
                if (selectAssignment) {
                    toolWindow.globals.selectedAssignmentID = ""
                    ProjectComponents(toolWindow.project).saveProjectComponents("")
                }

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

    private fun selectAssignment(assignmentLineToAdd: AssignmentTableLine): AssignmentTableLine {
        //unselect the current from table
        toolWindow.tableModel?.items?.find { it.radioButton.isSelected }?.radioButton?.isSelected = false
        assignmentLineToAdd.radioButton.isSelected = true
        toolWindow.globals.selectedLine = assignmentLineToAdd
        //save selected assigment in project metadata
        ProjectComponents(toolWindow.project).saveProjectComponents(assignmentLineToAdd.id_notVisible)
        return assignmentLineToAdd
    }

    private fun formatAssignmentToTableLine(): AssignmentTableLine {

        val line = AssignmentTableLine()

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


        return line
    }


}