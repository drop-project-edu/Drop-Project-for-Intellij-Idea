package org.dropProject.dropProjectPlugin.toolWindow.panel

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.ui.components.panels.NonOpaquePanel
import org.dropProject.dropProjectPlugin.actions.*
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import javax.swing.BorderFactory

class ToolbarPanel(private var toolWindow: DropProjectToolWindow) : NonOpaquePanel() {
    private var actionToolbar: ActionToolbar
    private var leftActionGroup = DefaultActionGroup()
    private var rightActionGroup = DefaultActionGroup()

    @NonNls
    private var openBuildReportActionID: String = ""

    init {

        layout = BorderLayout()//FlowLayout(FlowLayout.RIGHT)
        border = BorderFactory.createEmptyBorder()
        actionToolbar = this.createLeftToolbar()
        actionToolbar.targetComponent = this
        add(actionToolbar.component, BorderLayout.WEST)

        actionToolbar = this.createRightToolbar()
        actionToolbar.targetComponent = this
        add(actionToolbar.component, BorderLayout.EAST)

    }


    private fun createLeftToolbar(): ActionToolbar {
        if (toolWindow.authentication.alreadyLoggedIn) {
            leftActionGroup.add(AddAssignment(toolWindow))
            leftActionGroup.addSeparator()
            leftActionGroup.add(SubmitAssignment(toolWindow))
            leftActionGroup.addSeparator()
            leftActionGroup.add(RefreshList(toolWindow))
        } else {
            leftActionGroup.add(MainLogin(toolWindow))
        }

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, leftActionGroup, true)
    }

    private fun createRightToolbar(): ActionToolbar {
        if (toolWindow.authentication.alreadyLoggedIn) {
            if (toolWindow.globals.lastBuildReport != null) {

                val openBuildReportAction = CheckLastReport(toolWindow.globals.lastBuildReport, toolWindow)
                //GET ID FROM BUILD REPORT ACTION TO PROGRAMMATICALLY DISABLE
                //not using yet
                openBuildReportActionID = ActionManager.getInstance().getId(openBuildReportAction) ?: ""
                rightActionGroup.add(openBuildReportAction)
                rightActionGroup.addSeparator()
            }
            rightActionGroup.add(Logout(toolWindow))

        }
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, rightActionGroup, true)
    }

    fun loggedInToolbar() {

        leftActionGroup.removeAll()
        rightActionGroup.removeAll()

        leftActionGroup.add(AddAssignment(toolWindow))
        leftActionGroup.addSeparator()
        leftActionGroup.add(SubmitAssignment(toolWindow))
        leftActionGroup.addSeparator()
        leftActionGroup.add(RefreshList(toolWindow))

        rightActionGroup.add(Logout(toolWindow))
    }

    fun loggedOutToolbar() {
        leftActionGroup.removeAll()
        rightActionGroup.removeAll()
        leftActionGroup.add(MainLogin(toolWindow))
    }

    fun buildReportAvailable() {

        rightActionGroup.removeAll()

        rightActionGroup.add(CheckLastReport(toolWindow.globals.lastBuildReport, toolWindow))
        rightActionGroup.addSeparator()
        rightActionGroup.add(Logout(toolWindow))
    }

    fun hideBuildReportAction() {
        rightActionGroup.removeAll()
        rightActionGroup.addSeparator()
        rightActionGroup.add(Logout(toolWindow))

    }


}