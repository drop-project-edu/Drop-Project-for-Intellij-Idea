package com.tfc.ulht.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.ZipFolder
import com.tfc.ulht.dropProjectPlugin.submissionComponents.SubmissionReport
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.swing.JOptionPane


class SubmitAssignment(private var toolWindow: DropProjectToolWindow) : DumbAwareAction(
    "Submit Selected Assignment", "Submit an assignment to Drop Project", AllIcons.Actions.Upload
) {

    private val REQUEST_URL: String
        get() {
            return "${toolWindow.globals.REQUEST_URL}/api/student/submissions/new"
        }
    private var submissionId: SubmissionId? = null
    private var submissionResultsService = SubmissionReport(toolWindow)
    private var previousCheckTime: Long = 0

    override fun actionPerformed(e: AnActionEvent) {

        if (!toolWindow.authentication.alreadyLoggedIn) {
            // If user is has not logged in, show an error message
            JOptionPane.showMessageDialog(
                null, "You need to login before submitting an assignment", "Submit", JOptionPane.ERROR_MESSAGE
            )

        } else if (toolWindow.globals.selectedAssignmentID.isEmpty()) {
            // Before trying to submit project, check if an assignment has been chosen
            JOptionPane.showMessageDialog(
                null, "You need to choose an assignment first", "Unassigned Submission", JOptionPane.INFORMATION_MESSAGE
            )
        } else {
            // If assignment has been choosen, upload zip file
            //first save all documents
            FileDocumentManager.getInstance().saveAllDocuments()
            val uploadFilePath = ZipFolder(toolWindow.studentsList).zipIt(e) ?: return

            val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
                "file",
                "projeto.zip",
                File(uploadFilePath).asRequestBody("application/octet-stream".toMediaTypeOrNull())
            ).addFormDataPart("assignmentId", toolWindow.globals.selectedAssignmentID).build()

            val request: Request = Request.Builder().url(REQUEST_URL).method("POST", body).build()
            val moshi = Moshi.Builder().build()
            val submissionJsonAdapter = moshi.adapter(SubmissionId::class.java)
            toolWindow.authentication.httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    if (response.code == 200) {

                        submissionId = submissionJsonAdapter.fromJson(response.body!!.source())
                        DefaultNotification.notify(
                            e.project,
                            "<html>The submission from the assignment " + "<b>${toolWindow.globals.selectedAssignmentID}</b> has been submitted and is in validation. Please wait...</html>"
                        )
                        //DISABLE OPEN BUILD REPORT ACTION
                        toolWindow.globals.lastBuildReport = null


                    }
                } else if (response.code == 500) {
                    val errorJsonAdapter = moshi.adapter(ErrorMessage::class.java)
                    val errorMessage = errorJsonAdapter.fromJson(response.body!!.source())!!
                    Messages.showMessageDialog(errorMessage.error, "Submission", Messages.getErrorIcon())
                } else if (response.code == 403) {
                    val responseBody = response.body?.string()
                    val accessDeniedMessage = responseBody?.trim() ?: "Access Denied : Unknown error"
                    Messages.showMessageDialog(
                        accessDeniedMessage.replace("Access denied:", "").trim(),
                        "Access Denied",
                        Messages.getErrorIcon()
                    )

                } else if (response.code == 401) {
                    Messages.showMessageDialog(
                        "You're not authorized to submit this assignment", "Invalid Token", Messages.getErrorIcon()
                    )
                }


            }
        }
    }

    override fun update(e: AnActionEvent) {
        if (submissionId != null && (System.currentTimeMillis() - previousCheckTime > 8000)) {
            val task = object : Task.Backgroundable(e.project, "Fetching build report") {
                override fun run(p0: ProgressIndicator) {
                    if (submissionResultsService.checkResult(submissionId, e)) {
                        //ARRIVED
                        submissionId = null
                    }
                    previousCheckTime = System.currentTimeMillis()
                }

            }
            ApplicationManager.getApplication().invokeLater {
                // Choose the BGT thread for updating the UI
                task.asBackgroundable()
                task.queue()
            }

        }
    }
}

@JsonClass(generateAdapter = true)
data class SubmissionId(
    @Json(name = "submissionId") val submissionNumber: Int
)

@JsonClass(generateAdapter = true)
data class ErrorMessage(val error: String)

