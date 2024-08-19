package org.dropProject.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.dropProject.dropProjectPlugin.DefaultNotification
import org.dropProject.dropProjectPlugin.ZipFolder
import org.dropProject.dropProjectPlugin.submissionComponents.SubmissionReport
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.io.File
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.swing.JOptionPane


private const val POLLING_FREQUENCY = 8L  // seconds

class SubmitAssignment(private var toolWindow: DropProjectToolWindow) : DumbAwareAction(
    "Submit Selected Assignment", "Submit an assignment to Drop Project", AllIcons.Actions.Upload
) {

    private val logger = Logger.getInstance(SubmitAssignment::class.java)

    private val REQUEST_URL: String
        get() {
            return "${toolWindow.globals.REQUEST_URL}/api/student/submissions/new"
        }
    private var submissionId: SubmissionId? = null
    private var submissionResultsService = SubmissionReport(toolWindow)

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

            logger.info("Calling API: ${REQUEST_URL}")

            toolWindow.authentication.httpClient.newCall(request).execute().use { response ->
                logger.info("Received response: ${response}")
                if (response.isSuccessful) {
                    if (response.code == 200) {

                        submissionId = submissionJsonAdapter.fromJson(response.body!!.source())
                        logger.info("Set submission id: ${submissionId}")
                        DefaultNotification.notify(
                            e.project,
                            "<html>The submission from the assignment " + "<b>${toolWindow.globals.selectedAssignmentID}</b> has been submitted and is in validation. Please wait...</html>"
                        )
                        //DISABLE OPEN BUILD REPORT ACTION
                        toolWindow.globals.lastBuildReport = null

                        val task = object : Task.Backgroundable(e.project, "Validating submission") {
                            override fun run(indicator: ProgressIndicator) {

                                logger.info("task::run called")

                                indicator.isIndeterminate = true
                                val executor = Executors.newSingleThreadScheduledExecutor()
                                try {
                                    val future = executor.scheduleAtFixedRate({
                                        if (indicator.isCanceled) {
                                            // Stop the task if it's canceled
                                            logger.info("task::run was canceled by the user")
                                            executor.shutdown()
                                        } else if (submissionResultsService.checkResult(submissionId, e)) {
                                            // Stop the task if the condition is met
                                            submissionId = null
                                            logger.info("task::run checkResult was true")
                                            executor.shutdown()
                                        }
                                    }, 0, POLLING_FREQUENCY, TimeUnit.SECONDS)

                                    // Wait for the task to complete (optional)
                                    future.get()
                                } catch (e: CancellationException) {
                                    logger.trace("Task was canceled either because it finished or because the user cancelled")
                                } catch (e: Exception) {
                                    logger.error("An error occurred during task execution", e)
                                } finally {
                                    if (!executor.isShutdown) {
                                        executor.shutdown()
                                    }
                                }
                            }

                        }

                        logger.info("ProgressManager.getInstance().run(task)")
                        ProgressManager.getInstance().run(task)


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

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}

@JsonClass(generateAdapter = true)
data class SubmissionId(
    @Json(name = "submissionId") val submissionNumber: Int
)

@JsonClass(generateAdapter = true)
data class ErrorMessage(val error: String)

