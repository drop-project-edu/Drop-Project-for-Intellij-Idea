package org.dropProject.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow

class GoBack(private val toolWindow: DropProjectToolWindow) :
    DumbAwareAction("Go Back", "Go to previous", AllIcons.Actions.Back) {
    override fun actionPerformed(e: AnActionEvent) {
        toolWindow.switchToMainToolbar()
    }

}