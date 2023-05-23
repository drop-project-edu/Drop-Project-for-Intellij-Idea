package com.tfc.ulht.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.tfc.ulht.dropProjectPlugin.AuthorsFile
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.loginComponents.Login
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow

class MainLogin(private var toolWindow: DropProjectToolWindow) :
    DumbAwareAction("Login", "Insert your credentials", AllIcons.General.User) {


    override fun actionPerformed(e: AnActionEvent) {


        val projectDirectory = e.project?.let { FileEditorManager.getInstance(it).project.basePath.toString() }


        if (!toolWindow.authentication.alreadyLoggedIn) {
            Login(toolWindow)

            if (toolWindow.authentication.alreadyLoggedIn) {
                AuthorsFile(toolWindow.studentsList).make(projectDirectory, false, e)
            }

        } else {
            DefaultNotification.notify(e.project, "You are already logged in")
        }

        if (toolWindow.authentication.alreadyLoggedIn)
            toolWindow.updateAssignmentList()

    }
}