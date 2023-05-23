package com.tfc.ulht.dropProjectPlugin.toolWindow


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.tfc.ulht.dropProjectPlugin.actions.OpenSettings
import org.jetbrains.annotations.NotNull

class DropProjectToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(@NotNull project: Project, @NotNull toolWindow: ToolWindow) {
        ApplicationManager.getApplication().invokeLater {
            val panel = DropProjectToolWindow(project)
            val contentFactory = ContentFactory.getInstance()
            val content = contentFactory.createContent(panel.getContent(), "", false)
            toolWindow.contentManager.addContent(content)

            toolWindow.setTitleActions(listOf(OpenSettings(project)))
        }


    }
}