package org.dropProject.dropProjectPlugin.actions


import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.time.Duration
import java.util.concurrent.TimeUnit


class RefreshList(private var toolWindow: DropProjectToolWindow) :
    DumbAwareAction("Refresh Assigment List", "Refresh the assignment list", AllIcons.Actions.Refresh) {

    private val rateLimiter: RateLimiter
    private var coolOffTimer: Long? = null

    init {
        rateLimiter = createRateLimiter()
    }

    override fun actionPerformed(e: AnActionEvent) {
        try {
            if (coolOffTimer == null) {
                rateLimiter.executeCallable {
                    toolWindow.updateAssignmentList()
                }
            } else {
                if (System.currentTimeMillis() - coolOffTimer!! > 10000) {
                    coolOffTimer = null
                    rateLimiter.executeCallable {
                        toolWindow.updateAssignmentList()
                    }
                } else {

                    Messages.showMessageDialog(
                        "Number of requests was exceeded, wait " +
                                "${
                                    10 - TimeUnit
                                        .MILLISECONDS
                                        .toSeconds(System.currentTimeMillis() - coolOffTimer!!)
                                } seconds",
                        "Request Unauthorized",
                        Messages.getErrorIcon()
                    )
                }
            }

        } catch (e: RequestNotPermitted) {
            Messages.showMessageDialog(
                "Number of requests exceeded, wait 10 seconds",
                "Request Unauthorized",
                Messages.getErrorIcon()
            )
            coolOffTimer = System.currentTimeMillis()

        }


    }

    fun createRateLimiter(): RateLimiter {
        val config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(10))
            .limitForPeriod(2)
            .timeoutDuration(Duration.ofMillis(25))
            .build()

        //registry
        val rateLimiterRegistry = RateLimiterRegistry.of(config)

        return rateLimiterRegistry.rateLimiter("RefreshListRateLimiter")
    }
}