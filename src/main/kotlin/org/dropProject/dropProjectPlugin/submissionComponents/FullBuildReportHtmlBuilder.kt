package org.dropProject.dropProjectPlugin.submissionComponents

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import data.FullBuildReport
import org.dropProject.dropProjectPlugin.VirtualFile
import org.dropProject.dropProjectPlugin.actions.SubmissionId
import javax.swing.JEditorPane

enum class ErrorType {
    PS, C, TT
}

class FullBuildReportHtmlBuilder(
    private val fullbuildreport: FullBuildReport,
    private val submissionNum: SubmissionId? = null
) {
    var html: String = ""
    var errorType: ErrorType? = null
    var hasCheckStyleErrors: Boolean = false

    init {
        buildHtml()
    }

    fun buildHtml() {
        html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                htmlStyle() +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>Build report for ${fullbuildreport.assignment!!.name}</h1>" +
                (if (submissionNum != null) {
                    "<h2 style=\"padding-bottom:15px;\">Submission ${submissionNum.submissionNumber}</h2>"
                } else {
                    ""
                }) +
                "<span><b>Assignment id:</b> ${fullbuildreport.assignment.id} </span>" +
                "<br><br><span><b>Submitted:</b> ${fullbuildreport.submission!!.submissionDate.split(".")[0].split("T")[0]} </span>" +
                " ${fullbuildreport.submission.submissionDate.split(".")[0].split("T")[1]}</h3>\n<br><br>" +
                "<h2>Results Summary</h2>" +
                buildSummaryTable() +
                "<br>" +
                errorsDescriptionTable() +
                "<br>" +
                (if (fullbuildreport.buildReport?.checkstyleErrors?.isNotEmpty() == true) checkStyleTable() else "") +
                "</body>\n" +
                "</html>\n"
    }

    private fun htmlStyle(): String {
        return "<style>" +
                "body {font-family: arial, sans-serif;padding:10px;color:#333339;background-color:white;max-width: 760px; margin: auto;text-align:center;}\n" +
                "h1 {padding-bottom:0px;}" +
                "table {margin:auto;width:50%;border-collapse: collapse;border: 2px solid;border-color:#dddddd;}" +
                "td, th {border: 2px solid;border-color:#dddddd;text-align: left;padding: 20px 10px 20px 10px;}" +
                "</style>"
    }

    fun buildSummaryTable(): String {
        val table = StringBuilder("<table>")
        val checkStyle = "style=\"color:green;font-weight: bold;font-size: 24px;\""
        val errorStyle = "style=\"color:red;font-weight: bold;font-size: 24px;\""
        for (summaryElement in fullbuildreport.summary!!) {
            when (summaryElement.reportKey) {
                "PS" -> if (summaryElement.reportValue == "OK") {
                    table.append("<tr><th>Project Structure</th><td $checkStyle>✅</td></tr>")
                } else {
                    table.append("<tr><th>Project Structure ⚠️</th><td $errorStyle>❌</td></tr>")
                    errorType = ErrorType.PS
                    break
                }

                "C" -> if (summaryElement.reportValue == "OK") {
                    table.append("<tr><th>Compilation</th><td $checkStyle>✅</td></tr>")
                } else {
                    table.append("<tr><th>Compilation ⚠️</th><td $errorStyle>❌</td></tr>")
                    errorType = ErrorType.C
                    break
                }

                "TT" -> if (summaryElement.reportValue == "OK") {
                    table.append("<tr><th>Teacher Unit Tests</th><td $checkStyle>✅ ${summaryElement.reportProgress}/${summaryElement.reportGoal}</td></tr>")
                } else { //OK NOK ..?.. NULL??...CHECK THIS
                    table.append("<tr><th>Teacher Unit Tests ⚠️</th><td $errorStyle>❌ ${summaryElement.reportProgress}/${summaryElement.reportGoal}</td></tr>")
                    errorType = ErrorType.TT
                    break
                }

                "CS" -> if (summaryElement.reportValue == "OK") {
                    table.append("<tr><th>Code Quality (Checkstyle)</th><td $checkStyle>✅</td></tr>")
                } else {
                    table.append("<tr><th>Code Quality (Checkstyle) ⚠️</th><td $errorStyle>❌</td></tr>")
                    //checkstyle its different because it can combine with other errors
                    hasCheckStyleErrors = true
                    println("checkstyle enter")
                    break
                }
            }
        }
        table.append("</table>")
        return table.toString()
    }

    fun errorsDescriptionTable(): String {
        val table = StringBuilder("<table>")
        val style = "style=\"padding:10px;\""
        when (errorType) {
            ErrorType.PS -> {
                table.append("<thead><tr><th>${fullbuildreport.structureErrors!!.size} Structure Errors</th></tr></thead>")
                table.append("<tbody>")
                for (error in fullbuildreport.structureErrors) {
                    table.append("<tr><td $style>${error}</td></tr>")
                }
                table.append("</tbody>")
            }

            ErrorType.C -> {
                table.append("<thead><tr><th>Compilation Errors</th></tr></thead>")
                table.append("<tbody>")
                for (error in fullbuildreport.buildReport!!.compilationErrors!!) {
                    if (error.contains("[TEST]")) {
                        table.append(
                            "</td></tr>" +
                                    "<tr><td $style><p>${error}</p>"
                        )
                    } else {
                        table.append("<br><p>${error}</p>")
                    }
                }
                table.append("</tbody>")
            }

            ErrorType.TT -> {
                table.append("<thead><tr><th>Teacher Unit Tests Errors</th></tr></thead>")
                table.append("<tbody>")
                for (error in fullbuildreport.buildReport!!.junitErrorsTeacher!!.split("ERROR:|FAILURE:".toRegex())) {
                    if (error.isNotBlank()) {
                        table.append(
                            "<tr><td $style><p><code>${
                                error.replace("<", "&lt;").replace("\n", "<br>")
                            }</code></p></td></tr>"
                        )
                    }
                }
                table.append("</tbody>")
            }

            else -> {
                if (!hasCheckStyleErrors) {
                    table.append("<thead><tr><th>JUnit Summary</th></tr></thead>")
                    table.append("<tbody>")
                    table.append("<tr><td $style>${fullbuildreport.buildReport!!.junitSummaryTeacher}</td></tr>")
                    table.append("</tbody>")
                } else {
                    //remove table init tag
                    table.replace(0, 7, "")
                }

            }
        }
        if (!hasCheckStyleErrors)
            table.append("</table>")
        return table.toString()
    }

    fun checkStyleTable(): String {
        val table = StringBuilder("<table>")
        val style = "style=\"padding:10px;\""
        table.append("<thead>")
        table.append("<tr><th>Checkstyle Errors</th></tr></thead>")
        table.append("<tbody>")
        for (error in fullbuildreport.buildReport!!.checkstyleErrors!!) {
            table.append("<tr><td $style>${error}</td></tr>")
        }
        table.append("</tbody>")
        table.append("</table>")
        return table.toString()

    }

    fun show(project: Project?) {
        val editor = JEditorPane("text/html", html) // PREPARE HTML VIEWER
        editor.isEditable = false
        editor.caretPosition = 0
        editor.margin = JBUI.insets(0, 10, 10, 10)
        val jbScrollPane = JBScrollPane(editor)
        //val editor = UIBuildReport().buildComponents(fullbuildreport, submissionNum?.submissionNumber)
        //val editor = JEditorPane("text/html", html) // PREPARE HTML VIEWER
        //editor.isEditable = false
        //editor.caretPosition = 0
        //editor.margin = JBUI.insets(0, 10, 10, 10)
        //val jbScrollPane = JBScrollPane(editor)
        val editorManager = project?.let { FileEditorManager.getInstance(it) }
        val virtualFile = VirtualFile("Build Report", jbScrollPane)
        editorManager?.openFile(virtualFile, true)


    }
}