package com.tfc.ulht.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow


class AddAssignment(private val toolWindow: DropProjectToolWindow) :
    DumbAwareAction("Add Assignment", "Add assignment that is not present in the table", AllIcons.Actions.AddFile) {

    override fun actionPerformed(e: AnActionEvent) {
        toolWindow.switchToSearchToolbar(e)
    }
}