package org.dropProject.dropProjectPlugin.actions

import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBRadioButton
import com.jetbrains.rd.util.use
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import data.Assignment
import okhttp3.Request
import org.dropProject.dropProjectPlugin.assignmentComponents.AssignmentTableLine
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.lang.reflect.Type


class ListAssignment(private var toolWindow: DropProjectToolWindow) {

    private val type: Type = Types.newParameterizedType(
        List::class.java,
        Assignment::class.java
    )
    private val REQUEST_URL: String
        get() {
            return "${toolWindow.globals.REQUEST_URL}/api/student/assignments/current"
        }
    private var privateAssignments = listOf<Assignment>()
    private val moshi = Moshi.Builder().build()
    private val assignmentJsonAdapter: JsonAdapter<List<Assignment>> = moshi.adapter(type)
    private var status = 500


    fun getPrivateAssignments() {
        val request = Request.Builder()
            .url(REQUEST_URL)
            .build()
        toolWindow.authentication.httpClient.newCall(request).execute().use { response ->
            status = response.code
            if (status == 200) {
                privateAssignments = assignmentJsonAdapter.fromJson(response.body!!.source())!!
            }


        }
        val assignments = listAssignments()
        toolWindow.tableModel?.items = assignments
        toolWindow.resultsTable?.updateColumnSizes()

        toolWindow.resultsTable?.emptyText?.text = "No Assignments available"

        if (status == 401) {
            toolWindow.resultsTable?.emptyText?.text = "Unauthorized to view assignments"
            Messages.showMessageDialog(
                "You're not authorized to access assignments",
                "Invalid Token",
                Messages.getErrorIcon()
            )
        }
    }

    private fun listAssignments(): List<AssignmentTableLine> {
        val assignments: ArrayList<AssignmentTableLine> = ArrayList()

        for (assignment in privateAssignments) {
            val line = AssignmentTableLine()


            line.name = assignment.name
            line.language = assignment.language
            if (assignment.dueDate.isNullOrEmpty()) {
                line.dueDate = "No due date"
            } else {
                line.dueDate = assignment.dueDate.toString()
            }
            line.id_notVisible = assignment.id
            line.radioButton = JBRadioButton()
            line.instructions = assignment.instructions
            if (toolWindow.globals.selectedAssignmentID == line.id_notVisible) {
                line.radioButton.isSelected = true
                toolWindow.globals.selectedLine = line
            }

            assignments.add(line)

        }
        return assignments
    }


}