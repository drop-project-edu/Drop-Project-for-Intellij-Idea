package org.dropProject.dropProjectPlugin.submissionComponents


import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.wm.ToolWindowManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import data.FullBuildReport
import okhttp3.Request
import org.dropProject.dropProjectPlugin.BuildReportNotification
import org.dropProject.dropProjectPlugin.actions.SubmissionId
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow


class SubmissionReport(private val toolWindow: DropProjectToolWindow) {

    private val logger = Logger.getInstance(SubmissionReport::class.java)

    private val REQUEST_URL: String
        get() {
            return "${toolWindow.globals.REQUEST_URL}/api/student/submissions"
        }
    private lateinit var fullBuildReport: FullBuildReport
    private val moshi = Moshi.Builder().build()
    private val submissionJsonAdapter: JsonAdapter<FullBuildReport> = moshi.adapter(FullBuildReport::class.java)

    fun checkResult(submissionID: SubmissionId?, e: AnActionEvent): Boolean {

        if (submissionID != null) {

            val request = Request.Builder()
                .url("$REQUEST_URL/${submissionID.submissionNumber}")
                .build()

            logger.info("Calling API: $REQUEST_URL/${submissionID.submissionNumber}")

            toolWindow.authentication.httpClient.newCall(request).execute().use { response ->
                fullBuildReport = submissionJsonAdapter.fromJson(response.body!!.source())!!
                logger.info("Received response: ${fullBuildReport}")
            }

            logger.info("Received response2: ${fullBuildReport}")
            if (fullBuildReport.error == null) {
                BuildReportNotification(toolWindow.globals).notify(
                    e.project,
                    fullBuildReport,
                    submissionNum = submissionID,
                    dropProjectToolWindow = toolWindow
                )
                return true
            }

        }
        return false
    }

}


class ShowFullBuildReport(
    private val fullBuildReport: FullBuildReport,
    private val submissionNum: SubmissionId? = null,
    private val dropProjectToolWindow: DropProjectToolWindow
) :
    DumbAwareAction("Show") {
    override fun actionPerformed(e: AnActionEvent) {
        //editor file method
        // FullBuildReportHtmlBuilder(fullBuildReport, submissionNum = submissionNum).show(e.project)
        val toolWindow = e.project?.let { ToolWindowManager.getInstance(it).getToolWindow("Drop Project") }
        val contentManager = toolWindow?.contentManager
        contentManager?.let {
            val tabbedPane = dropProjectToolWindow.tabbedPane
            val newTabContent =
                UIBuildReport(dropProjectToolWindow.globals.REQUEST_URL).buildComponents(
                    fullBuildReport,
                    submissionNum?.submissionNumber
                )
            tabbedPane.setTabComponentAt(
                tabbedPane.tabCount,
                dropProjectToolWindow.createTabComponent(
                    tabbedPane,
                    if (submissionNum == null) {
                        "Last Build Report"
                    } else {
                        "Build report #${submissionNum.submissionNumber}"
                    },
                    AllIcons.Actions.Annotate,
                    newTabContent,
                    true
                )
            )
        }
    }


}