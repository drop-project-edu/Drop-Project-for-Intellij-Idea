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

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.util.Consumer
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.text.DateFormatUtil
import com.intellij.util.text.JBDateFormat
import com.intellij.vcs.log.runInEdt
import com.tfc.ulht.dropProjectPlugin.Globals
import java.awt.Component
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit

class PluginStatusWidget : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var statusBar: StatusBar? = null

    init {
        val future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay({
            runInEdt(this) { statusBar?.updateWidget(Globals.PLUGIN_ID) }
        }, 0, 1, TimeUnit.SECONDS)
        Disposer.register(this, Disposable { future.cancel(false) })
    }

    override fun ID(): String = Globals.PLUGIN_ID
    override fun getPresentation(): StatusBarWidget.WidgetPresentation? = this
    override fun getTooltipText(): String? {
        return JBDateFormat.getFormatter().formatDateTime(System.currentTimeMillis())
    }

    override fun getText(): String {
        return if (Globals.selectedAssignmentID.isEmpty()) {
            "No assignment selected"
        } else {
            "Selected assignment: ${Globals.selectedAssignmentID}"
        }
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getAlignment(): Float {
        return Component.CENTER_ALIGNMENT
    }

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        statusBar = null
    }




}