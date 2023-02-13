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

package com.tfc.ulht.dropProjectPlugin.loginComponents

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarPanel

class Logout() : DumbAwareAction("Logout", "Leave this login session", AllIcons.Actions.Exit) {

    override fun actionPerformed(e: AnActionEvent) {
        if (Authentication.alreadyLoggedIn) {
            val userMessage = Messages.showYesNoDialog(
                "Are you sure you want to logout?",
                "Logout", "Yes", "No", Messages.getQuestionIcon()
            )

            if (userMessage == 0) {
                Authentication.alreadyLoggedIn = false
                Globals.selectedAssignmentID = ""
                DefaultNotification.notify(e.project,"You've been logged out")

                ToolbarPanel.loggedOutToolbar()


                CredentialsController().removeStoredCredentials(Globals.PLUGIN_ID)
                DropProjectToolWindow.tableModel?.items = listOf()
                DropProjectToolWindow.resultsTable?.emptyText?.text = "Login to see your Assignments"
            }
        } else {
            Messages.showMessageDialog("You need to login first", "Logout", Messages.getInformationIcon())
        }
    }
    // TODO: update() , maybe its works on dynamic toolbar, *setVisible*
}

