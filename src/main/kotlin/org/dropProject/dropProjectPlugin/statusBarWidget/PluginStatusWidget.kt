@file:Suppress("MissingRecentApi")

package org.dropProject.dropProjectPlugin.statusBarWidget

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.text.DateFormatUtil
import java.awt.Component
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit

class PluginStatusWidget : StatusBarWidget, StatusBarWidget.TextPresentation {
    companion object {
        var idCount = 0
    }

    var selectedAssignmentID: String = ""
    private var statusBar: StatusBar? = null

    init {
        idCount++
        val future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay({
            ApplicationManager.getApplication().invokeLater {
                statusBar?.updateWidget("DropProjectStatusWidget$idCount")
            }
        }, 0, 1, TimeUnit.SECONDS)
        Disposer.register(this) { future.cancel(false) }
    }

    override fun ID(): String = "DropProjectStatusWidget$idCount"
    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this
    override fun getTooltipText(): String {
        return DateFormatUtil.formatDateTime(System.currentTimeMillis())
    }

    override fun getText(): String {
        return if (selectedAssignmentID.isEmpty()) {
            "No assignment selected"
        } else {
            "Selected assignment: $selectedAssignmentID"
        }
    }

    override fun getClickConsumer(): Consumer<MouseEvent> {
        return Consumer { }/*Consumer { mouseEvent ->
            var builder = JBPopupFactory.getInstance().createActionGroupPopup(
                null,
                DefaultActionGroup(DeselectAssignment()),
                DataManager.getInstance().getDataContext(mouseEvent.component),
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                true
            )
            builder.setMinimumSize(Dimension(110, 35))
            builder.showAbove(mouseEvent.component as JComponent)

        }*/
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

