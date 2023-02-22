package com.tfc.ulht.dropProjectPlugin.toolWindow.panel

import com.intellij.icons.AllIcons
import com.intellij.ide.plugins.newui.ListPluginComponent
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.intellij.ui.SearchTextField
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.panels.NonOpaquePanel
import com.jetbrains.rd.util.use
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.ProjectComponents
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.TableLine
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import data.Assignment
import data.AssignmentInfoResponse
import okhttp3.Request
import java.awt.FlowLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BorderFactory

class ToolbarSearchPanel(val e: AnActionEvent) : NonOpaquePanel() {

    companion object{
        lateinit var assignmentSearchField : SearchTextField
    }
    private var leftActionGroup:DefaultActionGroup
    private var rightActionGroup:DefaultActionGroup

    init {

        layout = FlowLayout(FlowLayout.LEFT)
        border = BorderFactory.createEmptyBorder()

        leftActionGroup = DefaultActionGroup()
        var toolbar = this.createLeftToolbar()
        toolbar.targetComponent = this
        add(toolbar.component)

        assignmentSearchField = SearchTextField()
        assignmentSearchField.textEditor.emptyText.appendText("Assignment ID",
            SimpleTextAttributes( SimpleTextAttributes.STYLE_PLAIN, ListPluginComponent.GRAY_COLOR)
        )
        assignmentSearchField.addKeyboardListener(keyAdapter(e))
        add(assignmentSearchField)

        rightActionGroup = DefaultActionGroup()
        toolbar = this.createRightToolbar()
        toolbar.targetComponent = this
        add(toolbar.component)

    }

   private fun keyAdapter(event: AnActionEvent): KeyAdapter {
        return object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (KeyEvent.VK_ENTER == e.keyCode) {
                    SearchAssignment().actionPerformed(event)
                }
            }
        }
    }

    private fun createLeftToolbar(): ActionToolbar {
        leftActionGroup.add(GoBack())
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR,leftActionGroup, true)
    }
    private fun createRightToolbar(): ActionToolbar {
        rightActionGroup.add(SearchAssignment())
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR,rightActionGroup, true)
    }


}

class GoBack : DumbAwareAction("Go Back","Go to previous",AllIcons.Actions.Back) {
    override fun actionPerformed(e: AnActionEvent) {
        DropProjectToolWindow.horizontalSplitter?.firstComponent = ToolbarPanel()
    }

}

class SearchAssignment : DumbAwareAction("Search Assignment",
    "Search for assignment by ID",AllIcons.Actions.Search) {
    private val REQUEST_URL = "${Globals.REQUEST_URL}/api/student/assignments"
    private val moshi = Moshi.Builder().build()
    private val assignmentJsonAdapter: JsonAdapter<AssignmentInfoResponse> = moshi.adapter(AssignmentInfoResponse::class.java)
    private var assignment: Assignment? = null
    private var errorCode: Int? = null
    override fun actionPerformed(e: AnActionEvent) {
        if (ToolbarSearchPanel.assignmentSearchField.text.isEmpty()){
            return
        }
        val request = Request.Builder()
            .url("$REQUEST_URL/${ToolbarSearchPanel.assignmentSearchField.text.trim()}")
            .build()

        Authentication.httpClient.newCall(request).execute().use { response ->
            if (response.code==200){
                val assignmentInfoResponse = assignmentJsonAdapter.fromJson(response.body!!.source())!!
                assignment = assignmentInfoResponse.assignment
                errorCode = assignmentInfoResponse.errorCode
            } else {
                errorCode = response.code
            }
        }
        if (assignment!=null){

            val assignmentLineToAdd = listAndSelectAssignment()

            if (assignmentLineToAdd == null) {
                Messages.showMessageDialog("This Assignment is already in your list",
                    "Duplicate Assignment", Messages.getWarningIcon())
            } else {
                val updateList = DropProjectToolWindow.tableModel!!.items.toMutableList()
                updateList.add(assignmentLineToAdd)
                DropProjectToolWindow.tableModel!!.items = updateList
                GoBack().actionPerformed(e)
                DefaultNotification.notify(e.project,
                    "<html>You've added <b>${assignmentLineToAdd.name}</b> to your list</html>")
            }
        } else {
            if (errorCode==404){
                Messages.showMessageDialog("Assignment not found", "Not Found", Messages.getErrorIcon())
            } else if (errorCode==403){
                Messages.showMessageDialog("You're not allowed to view this assignment",
                    "Access Denied", Messages.getErrorIcon())
            } else if (errorCode==401) {
                Messages.showMessageDialog("You're not authorized to access assignments",
                    "Invalid Token", Messages.getErrorIcon())
            }

        }
    }

    private fun listAndSelectAssignment(): TableLine? {

        val line = TableLine()

        line.name = assignment!!.name
        line.language = assignment!!.language
        if (assignment!!.dueDate.isNullOrEmpty()) {
            line.dueDate = "No due date"
        }
        else {
            line.dueDate = assignment!!.dueDate.toString()
        }
        line.id_notVisible = assignment!!.id
        line.instructions = assignment?.instructions
        line.radioButton = JBRadioButton()
        Globals.selectedAssignmentID = line.id_notVisible
        line.radioButton.isSelected = true
        Globals.selectedLine = line
        /*save project metadata*/
        ProjectComponents().saveProjectComponents(line.id_notVisible)
        DropProjectToolWindow.tableModel?.items?.forEach { it.radioButton.isSelected=false }

        if (DropProjectToolWindow.tableModel?.items?.contains(line) == true) {
            DropProjectToolWindow.tableModel?.items?.forEach {if (it.id_notVisible == line.id_notVisible){it.radioButton.isSelected=true} }
            return null
        }

        return line
    }

}

