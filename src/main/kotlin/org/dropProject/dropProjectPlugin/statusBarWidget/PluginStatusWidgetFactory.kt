package org.dropProject.dropProjectPlugin.statusBarWidget

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class PluginStatusWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = "DropProjectStatusWidget${PluginStatusWidget.idCount}"
    override fun getDisplayName(): String = "Drop Project Status Bar"
    override fun isAvailable(project: Project): Boolean = true
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
    override fun createWidget(project: Project): StatusBarWidget = PluginStatusWidget()
    override fun disposeWidget(widget: StatusBarWidget) {
        if (widget.ID() == id) Disposer.dispose(widget)
        PluginStatusWidget.idCount = 0
    }

}