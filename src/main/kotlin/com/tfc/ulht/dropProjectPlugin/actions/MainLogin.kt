/*-
 * Plugin Drop Project
 * 
 * Copyright (C) 2022 Yash Jahit & Bernardo Baltazar
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tfc.ulht.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.tfc.ulht.dropProjectPlugin.AuthorsFile
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.loginComponents.LoginDialog
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import javax.swing.JPanel

class MainLogin(private var toolWindow: DropProjectToolWindow) :
    DumbAwareAction("Login", "Insert your credentials", AllIcons.General.User) {


    override fun actionPerformed(e: AnActionEvent) {


        val projectDirectory = e.project?.let { FileEditorManager.getInstance(it).project.basePath.toString() }
        val panel = JPanel()


        if (!toolWindow.authentication.alreadyLoggedIn) {
            LoginDialog(toolWindow.authentication).assembleDialog(panel, e)

            if (toolWindow.authentication.alreadyLoggedIn) {
                AuthorsFile().make(projectDirectory, false, e)
            }

        } else {
            DefaultNotification.notify(e.project, "You are already logged in")
            //toolWindow.toolbarPanel!!.loggedInToolbar()
        }

        if (toolWindow.authentication.alreadyLoggedIn)
            toolWindow.updateAssignmentList()

    }
}