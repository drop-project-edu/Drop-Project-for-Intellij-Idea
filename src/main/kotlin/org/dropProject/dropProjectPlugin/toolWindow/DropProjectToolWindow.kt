package org.dropProject.dropProjectPlugin.toolWindow

import AssignmentTableModel
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SideBorder
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.actionButton
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBInsets
import org.dropProject.dropProjectPlugin.DefaultNotification
import org.dropProject.dropProjectPlugin.Globals
import org.dropProject.dropProjectPlugin.ProjectComponents
import org.dropProject.dropProjectPlugin.User
import org.dropProject.dropProjectPlugin.actions.ListAssignment
import org.dropProject.dropProjectPlugin.actions.PanelRoute
import org.dropProject.dropProjectPlugin.actions.SearchAssignment
import org.dropProject.dropProjectPlugin.assignmentComponents.ListTable
import org.dropProject.dropProjectPlugin.loginComponents.Authentication
import org.dropProject.dropProjectPlugin.settings.SettingsState
import org.dropProject.dropProjectPlugin.toolWindow.panel.AssignmentTablePanel
import org.dropProject.dropProjectPlugin.toolWindow.panel.ToolbarPanel
import org.dropProject.dropProjectPlugin.toolWindow.panel.ToolbarSearchPanel
import javax.swing.BorderFactory
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel


class DropProjectToolWindow(var project: Project) {

    var studentsList = ArrayList<User>()
    var tableModel: AssignmentTableModel? = null
    var resultsTable: ListTable? = null
    var toolbarPanel: ToolbarPanel? = null
    val tabbedPane: JBTabbedPane = JBTabbedPane()
    private var toolbarSearchPanel: ToolbarSearchPanel? = null
    val globals: Globals = Globals(project, this)
    val authentication: Authentication = Authentication(this)
    private var listAssignments = ListAssignment(this)

    private var contentToolWindow: JPanel? = null
    private var horizontalSplitter: OnePixelSplitter? = null


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


        tabbedPane.tabComponentInsets = JBInsets(0, 0, 0, 0)
        tabbedPane.setTabComponentAt(
            0,
            createTabComponent(tabbedPane, "Assignment List", AllIcons.Actions.ListFiles, assignmentTablePanel, false)
        )

        horizontalSplitter = OnePixelSplitter(true, 0.04f)
        horizontalSplitter!!.border = BorderFactory.createEmptyBorder()
        horizontalSplitter!!.dividerPositionStrategy = Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE
        horizontalSplitter!!.setResizeEnabled(false)
        horizontalSplitter!!.firstComponent = toolbarPanel
        horizontalSplitter!!.secondComponent = tabbedPane
        (this.contentToolWindow as SimpleToolWindowPanel).add(horizontalSplitter)

        if (authentication.alreadyLoggedIn) {
            updateAssignmentList()
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
            //globals.selectedAssignmentID = ""
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

        listAssignments.getPrivateAssignments()


        val idsToRemove = mutableListOf<String>()
        SettingsState.getInstance().publicAssignments.forEach {
            if (SearchAssignment(
                    assignmentID = it, toolWindow = this, route = PanelRoute.LOGIN,
                    selectAssignment = it == globals.selectedAssignmentID
                ).searchAndUpdateAssignmentList() == null
            ) {
                idsToRemove.add(it)
            }
        }
        idsToRemove.forEach {
            SettingsState.getInstance().removePublicAssignment(it)
        }

        //previous selected private assignments that now are disabled are not verified here still
        if (globals.selectedAssignmentID.isNotEmpty() && tableModel!!.items.find { it.id_notVisible == globals.selectedAssignmentID } == null) {
            DefaultNotification.notify(
                project,
                "<html>The assignment <b>${globals.selectedAssignmentID}</b> is no longer available</html>"
            )
            //selected (private) assignment is not present in the assignments list
            //update status bar
            globals.selectedAssignmentID = ""
            //update metadata
            ProjectComponents(project).saveProjectComponents("")
        }
    }

    fun createTabComponent(
        tabbedPane: JBTabbedPane,
        title: String,
        icon: Icon,
        content: JComponent,
        closeable: Boolean
    ): JPanel {
        tabbedPane.addTab(title, icon, content)
        val panel = panel {
            row {
                icon(icon)
                if (closeable) {
                    label(title)
                } else {
                    label(title).bold()
                }
                if (closeable) actionButton(CloseTabAction(tabbedPane, content))
            }
        }
        panel.isOpaque = false
        tabbedPane.selectedComponent = content
        return panel
    }

    inner class CloseTabAction(private val tabbedPane: JBTabbedPane, private val content: JComponent) :
        AnAction(AllIcons.Actions.Close) {
        override fun actionPerformed(e: AnActionEvent) {
            val tabIndex = tabbedPane.indexOfComponent(content)
            if (tabIndex != -1) {
                tabbedPane.removeTabAt(tabIndex)
            }
        }

    }
}