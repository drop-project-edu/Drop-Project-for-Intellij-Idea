package com.tfc.ulht.dropProjectPlugin.assignmentComponents


import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.submissionComponents.ShowFullBuildReport

class RefreshList() : DumbAwareAction("Refresh Assigment List","Refresh the assignment list" , AllIcons.Actions.Refresh) {
    override fun actionPerformed(e: AnActionEvent) {
        ListAssignment(false).get()
    }
}

class CheckLastReport() : DumbAwareAction("Last Build Report","Check last build report",AllIcons.Actions.Annotate) {
    override fun actionPerformed(e: AnActionEvent) {
        if (Globals.lastBuildReport!=null){
            ShowFullBuildReport(Globals.lastBuildReport!!).actionPerformed(e)
        } else {
            Messages.showMessageDialog("Report not available", "Build Report", Messages.getInformationIcon())
        }
    }

}