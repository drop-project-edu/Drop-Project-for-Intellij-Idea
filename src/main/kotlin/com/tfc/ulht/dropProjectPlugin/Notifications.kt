package com.tfc.ulht.dropProjectPlugin

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.tfc.ulht.dropProjectPlugin.submissionComponents.ShowFullBuildReport
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarPanel
import data.FullBuildReport
import org.jetbrains.annotations.Nullable

object DefaultNotification {
    fun notify(
        @Nullable project: Project?,
        content: String
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Default Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .notify(project)
    }
}
object BuildReportNotification {
    fun notify(
        @Nullable project: Project?,
        fullBuildReport: FullBuildReport,
        content: String = "The Build Report of your submission is now available to view"
    ) {
        Globals.lastBuildReport = fullBuildReport
        //display report button in toolbar
        ToolbarPanel.buildReportAvailable()

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Build Report Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .addAction(ShowFullBuildReport(fullBuildReport))
            .notify(project)
    }
}