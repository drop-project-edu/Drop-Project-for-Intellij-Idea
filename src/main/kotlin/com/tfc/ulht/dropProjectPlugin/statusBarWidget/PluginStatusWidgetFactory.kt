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

@file:Suppress("MissingRecentApi")

package com.tfc.ulht.dropProjectPlugin.statusBarWidget

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.tfc.ulht.dropProjectPlugin.Globals

class PluginStatusWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = Globals.PLUGIN_ID
    override fun getDisplayName(): String = "Status Bar Clock"
    override fun isAvailable(project: Project): Boolean = true
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
    override fun createWidget(project: Project): StatusBarWidget = PluginStatusWidget()
    override fun disposeWidget(widget: StatusBarWidget) {
        if (widget.ID() == Globals.PLUGIN_ID) Disposer.dispose(widget)
    }

    /*private fun runInEdt(disposable: Disposable, action: () -> Unit) {
        //ApplicationManager.getApplication().invokeLater(action, { Disposer.isDisposed(disposable) })
        ApplicationManager.getApplication().invokeLater(action)
    }

    private fun runInEdt(action: () -> Unit) {
        //ApplicationManager.getApplication().invokeLater(action, { Disposer.isDisposed(disposable) })
        ApplicationManager.getApplication().invokeLater(action)
    }*/

}