package org.dropProject.dropProjectPlugin

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import data.FullBuildReport
import org.dropProject.dropProjectPlugin.actions.SubmissionId
import org.dropProject.dropProjectPlugin.submissionComponents.ShowFullBuildReport
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
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

class BuildReportNotification(private var globals: Globals) {
    fun notify(
        @Nullable project: Project?,
        fullBuildReport: FullBuildReport,
        content: String = "The Build Report of your submission is now available to view",
        submissionNum: SubmissionId,
        dropProjectToolWindow: DropProjectToolWindow
    ) {
        globals.lastBuildReport = fullBuildReport

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Build Report Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .addAction(ShowFullBuildReport(fullBuildReport, submissionNum, dropProjectToolWindow))
            .notify(project)
    }

    fun notifyError(
        @Nullable project: Project?,
        error: String?
    ) {

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Build Report Notification")
            .createNotification("Error validating submission: $error", NotificationType.ERROR)
            .notify(project)
    }
}