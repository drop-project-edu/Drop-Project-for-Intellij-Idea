package com.tfc.ulht.dropProjectPlugin.actions


import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow

class RefreshList(private var toolWindow: DropProjectToolWindow) :
    DumbAwareAction("Refresh Assigment List", "Refresh the assignment list", AllIcons.Actions.Refresh) {
    override fun actionPerformed(e: AnActionEvent) {
        toolWindow.updateAssignmentList()
    }
}