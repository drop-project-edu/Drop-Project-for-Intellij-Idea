package com.tfc.ulht.dropProjectPlugin.toolWindow

import AssignmentTableModel
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SideBorder
import com.tfc.ulht.dropProjectPlugin.*
import com.tfc.ulht.dropProjectPlugin.actions.ListAssignment
import com.tfc.ulht.dropProjectPlugin.actions.PanelRoute
import com.tfc.ulht.dropProjectPlugin.actions.SearchAssignment
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.AssignmentTableLine
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.ListTable
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import com.tfc.ulht.dropProjectPlugin.loginComponents.CredentialsController
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.AssignmentTablePanel
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarPanel
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarSearchPanel
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel

class DropProjectToolWindow(var project: Project) {

    private var allAssignmentsState = ArrayList<AssignmentTableLine>()
    var studentsList = ArrayList<User>()
    var tableModel: AssignmentTableModel? = null
    var resultsTable: ListTable? = null
    var toolbarPanel: ToolbarPanel? = null
    private var contentToolWindow: JPanel? = null
    private var horizontalSplitter: OnePixelSplitter? = null
    private var toolbarSearchPanel: ToolbarSearchPanel? = null

    val globals: Globals = Globals(project, this)
    val authentication: Authentication = Authentication(this)


    fun getContent(): JComponent? {
        return contentToolWindow
    }

    init {
        //toolbar
        toolbarPanel = ToolbarPanel(this)
        toolbarPanel!!.border = IdeBorderFactory.createBorder(SideBorder.TOP or SideBorder.RIGHT or SideBorder.BOTTOM)
        //on start login
        //launchLogin()
        //toolwindow builder
        contentToolWindow = SimpleToolWindowPanel(true, true)

        tableModel = AssignmentTableModel(AssignmentTableModel.generateColumnInfo(), ArrayList())
        resultsTable = ListTable(this)
        val assignmentTablePanel = AssignmentTablePanel(resultsTable!!)
        assignmentTablePanel.border = IdeBorderFactory.createBorder(SideBorder.TOP or SideBorder.RIGHT)

        horizontalSplitter = OnePixelSplitter(true, 0.0f)
        horizontalSplitter!!.border = BorderFactory.createEmptyBorder()
        horizontalSplitter!!.dividerPositionStrategy = Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE
        horizontalSplitter!!.setResizeEnabled(false)
        horizontalSplitter!!.firstComponent = toolbarPanel
        horizontalSplitter!!.secondComponent = assignmentTablePanel
        (this.contentToolWindow as SimpleToolWindowPanel).add(horizontalSplitter)

        if (authentication.alreadyLoggedIn) {
            updateAssignmentList()
        }


    }

    private fun launchLogin() {
        val credentials = CredentialsController().retrieveStoredCredentials("DropProject")
        if (credentials != null) {
            credentials.getPasswordAsString()
                ?.let { credentials.userName?.let { it1 -> authentication.onStartAuthenticate(it1, it) } }
        }
    }

    private fun getAllAssignmentsState() {
        val allAssignments = AllAssignments.getInstance()
        for (id in allAssignments.getListOfIds()) {
            val assignmentTableLine = AssignmentTableLine()
            assignmentTableLine.id_notVisible = id
            this.allAssignmentsState.add(assignmentTableLine)
        }
    }

    private fun readMetadata() {
        val components = ProjectComponents(project).loadProjectComponents()
        if (components.selectedAssignmentID != null) {
            if (components.selectedAssignmentID!!.isNotEmpty()) {
                globals.selectedAssignmentID = components.selectedAssignmentID!!
                DefaultNotification.notify(
                    project,
                    "<html>The assignment <b>${globals.selectedAssignmentID}</b> was selected</html>"
                )
            }

        } else {
            globals.selectedAssignmentID = ""
            globals.selectedLine = null
        }
    }

    fun switchToSearchToolbar(e: AnActionEvent) {
        if (toolbarSearchPanel == null) toolbarSearchPanel = ToolbarSearchPanel(e, this)
        horizontalSplitter!!.firstComponent = toolbarSearchPanel
    }

    fun switchToMainToolbar() {
        horizontalSplitter!!.firstComponent = toolbarPanel
        toolbarSearchPanel!!.clearSearchText()
    }

    fun updateAssignmentList() {
        readMetadata()
        ListAssignment(this).get()
        val tableLine = AssignmentTableLine()
        tableLine.id_notVisible = globals.selectedAssignmentID
        if (!tableModel?.items!!.contains(tableLine)) {
            SearchAssignment(globals.selectedAssignmentID, this, PanelRoute.LOGIN).searchAndUpdateAssignmentList()
        }
        /* for (assignment in allAssignmentsState) {
             if (!tableModel?.items!!.contains(assignment)) {
                 if (SearchAssignment(
                         assignment.id_notVisible,
                         this,
                         PanelRoute.LOGIN
                     ).searchAndUpdateAssignmentList() == null
                 ) {
                     //assignment not found
                     allAssignmentsState.remove(assignment)
                     AllAssignments.getInstance().removeAssignmentID(assignment.id_notVisible)
                 }
             }
         }*/
    }

}