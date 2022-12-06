/*-
 * Plugin Drop Project
 * 
 * Copyright (C) 2019 Yash Jahit
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

import AssignmentTableModel
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.ListTable
import org.jetbrains.annotations.Nullable

class Logout(val tableModel: AssignmentTableModel, val resultsTable: ListTable) : DumbAwareAction("Logout", "Leave this login session", AllIcons.Actions.ShowReadAccess) {

    override fun actionPerformed(e: AnActionEvent) {
        if (Authentication.alreadyLoggedIn) {
            val userMessage = Messages.showYesNoDialog(
                "Are you sure you want to logout?",
                "Logout", "Yes", "No", Messages.getQuestionIcon()
            )

            if (userMessage == 0) {
                Authentication.alreadyLoggedIn = false
                Globals.selectedAssignmentID = ""
                LoggedOffNotifier.notify(e.project,"You've been logged out")

            }
        } else {
            Messages.showMessageDialog("You need to login first", "Logout", Messages.getInformationIcon())
        }
    }
    // TODO: update() , maybe its works on dynamic toolbar, *setVisible*
}

object LoggedOffNotifier {
    fun notify(
        @Nullable project: Project?,
        content: String
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Logged Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .notify(project)
    }
}