package com.tfc.ulht.dropProjectPlugin.submissionComponents


import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tfc.ulht.dropProjectPlugin.BuildReportNotification
import com.tfc.ulht.dropProjectPlugin.actions.SubmissionId
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import data.FullBuildReport
import okhttp3.Request


class SubmissionReport(private val toolWindow: DropProjectToolWindow) {

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

            toolWindow.authentication.httpClient.newCall(request).execute().use { response ->
                fullBuildReport = submissionJsonAdapter.fromJson(response.body!!.source())!!
            }

            if (fullBuildReport.error == null) {
                BuildReportNotification(toolWindow.globals).notify(
                    e.project,
                    fullBuildReport,
                    submissionNum = submissionID
                )
                return true
            }

        }
        return false
    }

}


class ShowFullBuildReport(
    private val fullBuildReport: FullBuildReport,
    private val submissionNum: SubmissionId? = null
) :
    DumbAwareAction("Show") {
    override fun actionPerformed(e: AnActionEvent) {
        FullBuildReportHtmlBuilder(fullBuildReport, submissionNum = submissionNum).show(e.project)
    }


}