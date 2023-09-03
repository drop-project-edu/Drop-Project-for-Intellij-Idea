package org.dropProject.dropProjectPlugin.toolWindow


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.dropProject.dropProjectPlugin.actions.OpenSettings
import org.jetbrains.annotations.NotNull

class DropProjectToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(@NotNull project: Project, @NotNull toolWindow: ToolWindow) {
        ApplicationManager.getApplication().invokeLater {
            val mainTabContent = DropProjectToolWindow(project)
            val contentFactory = ContentFactory.getInstance()
            val content = contentFactory.createContent(mainTabContent.getContent(), "", false)
            toolWindow.contentManager.addContent(content)
            toolWindow.setTitleActions(listOf(OpenSettings(project)))
        }


    }


}

