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

package com.tfc.ulht.dropProjectPlugin.submissionComponents


import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.BuildReportNotification
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.SubmissionId
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import data.FullBuildReport
import okhttp3.Request


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
                BuildReportNotification.notify(e.project,fullBuildReport)
                return true
            }

        }
        return false
    }

}



class ShowFullBuildReport(private val fullBuildReport: FullBuildReport) : DumbAwareAction("Show") {
    override fun actionPerformed(e: AnActionEvent) {
        val editorManager = e.project?.let { FileEditorManager.getInstance(it) }
        val virtualFile = BuildReportVirtualFile("Build Report",fullBuildReport)
        editorManager?.openFile(virtualFile,true)
    }


}