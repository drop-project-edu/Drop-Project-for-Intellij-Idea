package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import AssignmentTableModel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.rd.util.use
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import data.Assignment
import okhttp3.*
import java.lang.reflect.Type


class ListAssignment(val tableModel: AssignmentTableModel, val resultsTable: ListTable) {

    val type: Type = Types.newParameterizedType(
        List::class.java,
        Assignment::class.java
    )
    private val REQUEST_URL = "${Globals.REQUEST_URL}/api/student/assignments/current"
    private var assignmentList = listOf<Assignment>()
    private val moshi = Moshi.Builder().build()
    private val assignmentJsonAdapter: JsonAdapter<List<Assignment>> = moshi.adapter(type)


     fun get() {

        if (Authentication.alreadyLoggedIn) {
            val request = Request.Builder()
                .url(REQUEST_URL)
                .build()
            Authentication.httpClient.newCall(request).execute().use { response ->
                assignmentList = assignmentJsonAdapter.fromJson(response.body!!.source())!!

            }
            val assignments = listAssignments()
            tableModel.items = assignments
            resultsTable.updateColumnSizes()

            resultsTable.emptyText.text = "No Assignments available"

        }
        else {
            tableModel.items = listOf()
            resultsTable.emptyText.text = "Login to see your Assignments"
        }
    }

    private fun listAssignments(): List<TableLine> {
        val assignments : ArrayList<TableLine>  = ArrayList()

        for (assignment in assignmentList) {
            val line = TableLine()
            line.name = assignment.name
            line.language = assignment.language
            if (assignment.dueDate.isNullOrEmpty()) {
                line.dueDate = "No due date"
            }
            else {
                line.dueDate = assignment.dueDate.toString()
            }
            line.id_notVisible = assignment.id


            assignments.add(line)

        }
        return assignments
    }



}