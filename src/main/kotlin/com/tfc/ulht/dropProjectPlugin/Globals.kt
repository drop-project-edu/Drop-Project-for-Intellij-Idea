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

package com.tfc.ulht.dropProjectPlugin

import com.tfc.ulht.dropProjectPlugin.assignmentComponents.TableLine
import com.tfc.ulht.dropProjectPlugin.statusBarWidget.PluginStatusWidget
import data.FullBuildReport

class Globals {

    companion object {


        //val REQUEST_URL = "https://drop-project-fork.herokuapp.com"
        //val REQUEST_URL = "http://localhost:8080"
        val REQUEST_URL = "https://deisi.ulusofona.pt/drop-project"
        val PLUGIN_ID = PluginStatusWidget::class.java.name


        var selectedAssignmentID: String = ""
        var selectedLine: TableLine? = null

        var lastBuildReport: FullBuildReport? = null
    }

}