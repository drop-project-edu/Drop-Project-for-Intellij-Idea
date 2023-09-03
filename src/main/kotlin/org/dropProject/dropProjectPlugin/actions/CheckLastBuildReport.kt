package org.dropProject.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import data.FullBuildReport
import org.dropProject.dropProjectPlugin.submissionComponents.ShowFullBuildReport
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow

class CheckLastReport(
    private var lastBuildReport: FullBuildReport?,
    private val dropProjectToolWindow: DropProjectToolWindow
) :
    DumbAwareAction("Last Build Report", "Check last build report", AllIcons.Actions.Annotate) {
    override fun actionPerformed(e: AnActionEvent) {
        if (lastBuildReport != null) {
            ShowFullBuildReport(lastBuildReport!!, dropProjectToolWindow = dropProjectToolWindow).actionPerformed(e)
        } else {
            Messages.showMessageDialog("Report not available", "Build Report", Messages.getInformationIcon())
        }
    }

}