package com.tfc.ulht.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.tfc.ulht.dropProjectPlugin.submissionComponents.ShowFullBuildReport
import data.FullBuildReport

class CheckLastReport(private var lastBuildReport: FullBuildReport?) :
    DumbAwareAction("Last Build Report", "Check last build report", AllIcons.Actions.Annotate) {
    override fun actionPerformed(e: AnActionEvent) {
        if (lastBuildReport != null) {
            ShowFullBuildReport(lastBuildReport!!).actionPerformed(e)
        } else {
            Messages.showMessageDialog("Report not available", "Build Report", Messages.getInformationIcon())
        }
    }

}