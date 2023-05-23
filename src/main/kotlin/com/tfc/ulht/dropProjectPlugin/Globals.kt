package com.tfc.ulht.dropProjectPlugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.AssignmentTableLine
import com.tfc.ulht.dropProjectPlugin.settings.SettingsState
import com.tfc.ulht.dropProjectPlugin.statusBarWidget.PluginStatusWidget
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import data.FullBuildReport

class Globals(private val project: Project, private val toolWindow: DropProjectToolWindow) {

    val REQUEST_URL: String
        get() {
            return SettingsState.getInstance().serverURL
        }

    var statusWidgetId = "DropProjectStatusWidget${PluginStatusWidget.idCount}"
    var selectedAssignmentID: String = ""
        set(value) {
            field = value
            val statusWidget: PluginStatusWidget = WindowManager.getInstance().getStatusBar(project)
                .getWidget(statusWidgetId) as PluginStatusWidget

            statusWidget.selectedAssignmentID = value
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