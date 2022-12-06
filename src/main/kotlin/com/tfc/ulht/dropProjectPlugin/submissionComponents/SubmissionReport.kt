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

package com.tfc.ulht.dropProjectPlugin.submissionComponents


import assignmentTable.FullBuildReportTableColumn
import com.intellij.diagnostic.PluginException
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.SubmissionId
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import data.FullBuildReport
import okhttp3.Request
import org.jetbrains.annotations.Nullable


class SubmissionReport {

    private val REQUEST_URL = "${Globals.REQUEST_URL}/api/student/submissions"
    private lateinit var fullBuildReport : FullBuildReport
    private val moshi = Moshi.Builder().build()
    private val submissionJsonAdapter: JsonAdapter<FullBuildReport> = moshi.adapter(FullBuildReport::class.java)

    fun checkResult(submissionID: SubmissionId?, e : AnActionEvent): Boolean {

        if (submissionID!=null) {

            val request = Request.Builder()
                .url("$REQUEST_URL/${submissionID.submissionNumber}")
                .build()

                Authentication.httpClient.newCall(request).execute().use { response ->
                    fullBuildReport = submissionJsonAdapter.fromJson(response.body!!.source())!!
                }

            if (fullBuildReport.error == null){
                ReportResultsNotifier.notify(e.project,fullBuildReport)
                return true
            }

        }
        return false
    }


    private fun showFullBuildReport() {

        FullBuildReportTableColumn(fullBuildReport)
    }

}

object ReportResultsNotifier {
    fun notify(
        @Nullable project: Project?,
        fullBuildReport: FullBuildReport,
        content: String = "The Build Report of your submission is now available to view"
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Report Results Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .addAction(ShowFullBuildReport(fullBuildReport))
            .notify(project)
    }
}

class ShowFullBuildReport(private val fullBuildReport: FullBuildReport) : DumbAwareAction("Show") {
    override fun actionPerformed(e: AnActionEvent) {
        FullBuildReportTableColumn(fullBuildReport)
    }
}