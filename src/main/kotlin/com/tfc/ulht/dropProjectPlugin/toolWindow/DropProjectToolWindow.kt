package com.tfc.ulht.dropProjectPlugin.toolWindow

import AssignmentTableModel
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SearchTextField
import com.intellij.ui.SideBorder
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.ProjectComponents
import com.tfc.ulht.dropProjectPlugin.actions.ListAssignment
import com.tfc.ulht.dropProjectPlugin.actions.PanelRoute
import com.tfc.ulht.dropProjectPlugin.actions.SearchAssignment
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.ListTable
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.TableLine
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import com.tfc.ulht.dropProjectPlugin.loginComponents.CredentialsController
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.AssignmentTablePanel
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarPanel
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarSearchPanel
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel

class DropProjectToolWindow(var project: Project) {

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
        val credentials = CredentialsController().retrieveStoredCredentials("DropProject")
        if (credentials != null) {
            credentials.getPasswordAsString()
                ?.let { credentials.userName?.let { it1 -> authentication.onStartAuthenticate(it1, it) } }
        }
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

    private fun readMetadata() {
        val components = ProjectComponents(project).loadProjectComponents()
        if (components.selectedAssignmentID != null) {
            globals.selectedAssignmentID = components.selectedAssignmentID!!
            DefaultNotification.notify(
                project,
                "<html>The assignment <b>${globals.selectedAssignmentID}</b> was selected</html>"
            )
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
        val tableLine = TableLine()
        tableLine.id_notVisible = globals.selectedAssignmentID
        if (!tableModel?.items!!.contains(tableLine)) {
            val searchfieldDummy = SearchTextField()
            searchfieldDummy.text = globals.selectedAssignmentID
            SearchAssignment(searchfieldDummy, this, PanelRoute.LOGIN).searchAndUpdateAssignmentList()
        }
    }

}