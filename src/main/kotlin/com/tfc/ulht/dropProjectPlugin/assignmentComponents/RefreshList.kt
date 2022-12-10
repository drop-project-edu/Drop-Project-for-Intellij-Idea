package com.tfc.ulht.dropProjectPlugin.assignmentComponents


import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class RefreshList() : DumbAwareAction("Refresh","Refresh the assignment list" , AllIcons.Actions.Refresh) {
    override fun actionPerformed(e: AnActionEvent) {
        ListAssignment(false).get()
    }
}