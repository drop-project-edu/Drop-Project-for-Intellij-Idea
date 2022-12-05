package com.tfc.ulht.dropProjectPlugin.toolWindow


import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.NotNull

class DropProjectToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(@NotNull project: Project, @NotNull toolWindow: ToolWindow) {
        val panel = DropProjectToolWindow()
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(panel.getContent(),"",false)
        toolWindow.contentManager.addContent(content)



    }
}