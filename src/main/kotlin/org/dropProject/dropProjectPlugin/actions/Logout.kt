package org.dropProject.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import org.dropProject.dropProjectPlugin.DefaultNotification
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow

class Logout(private val toolWindow: DropProjectToolWindow) :
    DumbAwareAction("Logout", "Leave this login session", AllIcons.Actions.Exit) {

    override fun actionPerformed(e: AnActionEvent) {
        if (toolWindow.authentication.alreadyLoggedIn) {
            val userMessage = Messages.showYesNoDialog(
                "Are you sure you want to logout?",
                "Logout", "Yes", "No", Messages.getQuestionIcon()
            )

            if (userMessage == 0) {
                toolWindow.authentication.alreadyLoggedIn = false
                toolWindow.globals.selectedAssignmentID = ""
                DefaultNotification.notify(e.project, "You've been logged out")

                toolWindow.tableModel?.items = listOf()
                toolWindow.resultsTable?.emptyText?.text = "Login to see your Assignments"
            }
        } else {
            Messages.showMessageDialog("You need to login first", "Logout", Messages.getInformationIcon())
        }
    }
}

