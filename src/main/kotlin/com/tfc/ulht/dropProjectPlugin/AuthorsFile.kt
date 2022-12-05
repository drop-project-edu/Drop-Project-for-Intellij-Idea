package com.tfc.ulht.dropProjectPlugin


import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.tfc.ulht.dropProjectPlugin.loginComponents.LoginDialog.Companion.studentsList
import com.tfc.ulht.dropProjectPlugin.submissionComponents.ShowFullBuildReport
import data.FullBuildReport
import org.jetbrains.annotations.Nullable
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.swing.JOptionPane

class AuthorsFile {

    fun make(projectDirectory: String?, silentMode : Boolean, e : AnActionEvent) {


        val authors = File("$projectDirectory/AUTHORS.txt")

        if (authors.delete()) {
            if (!silentMode)
                AuthorsCreationNotifier.notify(e.project,"AUTHORS file was successfully modified")
        }
        else {
            if (!silentMode)
                AuthorsCreationNotifier.notify(e.project,"AUTHORS file was successfully created")
        }

        val writer = BufferedWriter(FileWriter("$projectDirectory/AUTHORS.txt"))
        for (students in studentsList) {
            writer.write(students.toString())
            writer.newLine()

        }
        writer.close()

    }

}

object AuthorsCreationNotifier {
    fun notify(
        @Nullable project: Project?,
        content: String
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Authors Created Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .notify(project)
    }
}
