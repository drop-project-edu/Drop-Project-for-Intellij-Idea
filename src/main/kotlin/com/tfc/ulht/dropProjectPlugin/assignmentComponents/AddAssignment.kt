package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarSearchPanel


class AddAssignment() : DumbAwareAction("Add Assignment","Add assignment that is not present in the table", AllIcons.Actions.AddFile) {

    override fun actionPerformed(e: AnActionEvent) {
        val searchToolbar = ToolbarSearchPanel(e)
        DropProjectToolWindow.horizontalSplitter?.firstComponent = searchToolbar

    }
}