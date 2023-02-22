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

package com.tfc.ulht.dropProjectPlugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.ListAssignment
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import com.tfc.ulht.dropProjectPlugin.loginComponents.CredentialsController
import com.tfc.ulht.dropProjectPlugin.toolWindow.panel.ToolbarPanel

class OnStartup : StartupActivity {

    override fun runActivity(project: Project) {

        val components = ProjectComponents().loadProjectComponents()
        if (components.selectedAssignmentID!=null){
            Globals.selectedAssignmentID = components.selectedAssignmentID!!
        } else {
            Globals.selectedAssignmentID=""
            Globals.selectedLine=null
        }

        val credentials = CredentialsController().retrieveStoredCredentials(Globals.PLUGIN_ID)
        if (credentials != null) {
            credentials.getPasswordAsString()
                ?.let { credentials.userName?.let { it1 -> Authentication().onStartAuthenticate(it1, it) } }
        }

        if (Authentication.alreadyLoggedIn){

            ListAssignment(false).get()
            ToolbarPanel.loggedInToolbar()
        }

    }


}