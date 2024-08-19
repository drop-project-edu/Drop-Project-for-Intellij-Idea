package org.dropProject.dropProjectPlugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import data.FullBuildReport
import org.dropProject.dropProjectPlugin.assignmentComponents.AssignmentTableLine
import org.dropProject.dropProjectPlugin.settings.SettingsState
import org.dropProject.dropProjectPlugin.statusBarWidget.PluginStatusWidget
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow

class Globals(private val project: Project, private val toolWindow: DropProjectToolWindow) {

    val REQUEST_URL: String
        get() {
            return SettingsState.getInstance().serverURL
        }

    var statusWidgetId = "DropProjectStatusWidget${PluginStatusWidget.idCount}"
    var selectedAssignmentID: String = ""
        set(value) {
            field = value
            val statusWidget: PluginStatusWidget? = WindowManager.getInstance().getStatusBar(project)
                .getWidget(statusWidgetId) as PluginStatusWidget?

            statusWidget?.selectedAssignmentID = value
        }

    var selectedLine: AssignmentTableLine? = null
        set(value) {
            field = value
            selectedAssignmentID = value?.id_notVisible ?: ""
        }
    var lastBuildReport: FullBuildReport? = null
        set(value) {
            field = value
            if (value != null) {
                toolWindow.toolbarPanel!!.buildReportAvailable()
            } else {
                toolWindow.toolbarPanel!!.hideBuildReportAction()
            }
        }

}