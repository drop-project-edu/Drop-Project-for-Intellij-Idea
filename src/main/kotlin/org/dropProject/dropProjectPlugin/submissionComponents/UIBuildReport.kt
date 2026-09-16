package org.dropProject.dropProjectPlugin.submissionComponents

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.actionsButton
import com.intellij.ui.dsl.builder.panel
import data.FullBuildReport
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.JViewport


internal class UIBuildReport(private val requestUrl: String) {
    fun buildComponents(buildReport: FullBuildReport, submissionNumber: Int?): JBScrollPane {
        val panel = panel {
            row {
                label("Build report").bold()
                submissionNumber?.let {
                    comment("Submission: $submissionNumber")
                }
                browserLink("Open in Web", "$requestUrl/buildReport/$submissionNumber")
            }
            row {
                comment("Assignment: ${buildReport.assignment!!.name}")
            }
            row {
                val fullDate = buildReport.submission!!.submissionDate.split(".")[0].split("T")
                comment("Submitted: ${fullDate[0]} | ${fullDate[1]}")
            }
            buildReport.summary?.let { self ->
                self.forEach { summary ->
                    row {
                        when (summary.reportKey) {
                            "PS" -> {
                                label("Project structure")
                                if (summary.reportValue == "OK") {
                                    icon(AllIcons.General.InspectionsOK)
                                } else {
                                    comment("<icon src='AllIcons.Actions.Suspend'>&nbsp;")
                                }
                            }

                            "C" -> {
                                label("Compilation")
                                if (summary.reportValue == "OK") {
                                    icon(AllIcons.General.InspectionsOK)
                                } else {
                                    comment("<icon src='AllIcons.Actions.Suspend'>&nbsp;")
                                }
                            }

                            "ST" -> {
                                label("Student unit tests")
                                if (summary.reportValue == "OK") {
                                    comment("<icon src='AllIcons.General.InspectionsOK'>&nbsp;<b>${summary.reportProgress}/${summary.reportGoal}</b>")
                                } else if (summary.reportValue == "NOK") {
                                    comment("<icon src='AllIcons.Actions.Suspend'>&nbsp;<b>${summary.reportProgress}/${summary.reportGoal}</b>")
                                } else {
                                    comment("<icon src='AllIcons.Gutter.Mnemonic'>&nbsp;<b>&nbsp;${summary.reportProgress}/${summary.reportGoal} (${summary.reportValue})</b>")
                                }
                            }

                            "TT" -> {
                                label("Teacher unit tests")
                                if (summary.reportValue == "OK") {
                                    comment("<icon src='AllIcons.General.InspectionsOK'>&nbsp;<b>${summary.reportProgress}/${summary.reportGoal}</b>")
                                } else { //OK NOK ..?.. NULL??...CHECK THIS
                                    comment("<icon src='AllIcons.Actions.Suspend'>&nbsp;<b>${summary.reportProgress}/${summary.reportGoal}</b>")
                                }
                            }

                            "CS" -> {
                                label("Code quality")
                                if (summary.reportValue == "OK") {

                                    icon(AllIcons.General.InspectionsOK)
                                } else {
                                    icon(AllIcons.Debugger.Db_no_suspend_breakpoint)
                                }
                            }
                        }
                    }.layout(RowLayout.PARENT_GRID)
                }
            }
            //PROJECT STRUCTURE
            buildReport.structureErrors?.let { psErr ->
                val errors = groupStructureErrors(psErr)
                collapsibleGroup("Project Structure Errors (${errors.size})") {
                    errors.forEachIndexed { index, error ->
                        group("Error ${index + 1}") {
                            row {
                                actionsButton(CopyAction(error.joinToString(System.lineSeparator())))
                                text(error.first() + error.drop(1).joinToString("") { "<br>&nbsp;&nbsp;$it" })
                            }
                        }
                    }
                }
            }
            //COMPILATION
            buildReport.buildReport?.compilationErrors?.let { cErr ->
                collapsibleGroup(
                    "Compilation Errors (${
                        cErr.filter { it.contains("[TEST]") }.size
                    })"
                ) {
                    cErr.joinToString("<br>").trim().split("[TEST]").forEach { error ->
                        if (error.isNotBlank()) {
                            group {
                                row {
                                    actionsButton(CopyAction(error))
                                    text(error)

                                }

                            }
                        }
                    }
                }
            }
            //CHECKSTYLE
            buildReport.buildReport?.checkstyleErrors?.let { csErr ->
                collapsibleGroup("Code Quality Errors (${csErr.size})") {
                    csErr.forEach { error ->
                        group {
                            row {
                                actionsButton(CopyAction(error))
                                text(error)
                            }
                        }
                    }
                }
            }
            //TEACHER UNIT TESTS
            buildReport.buildReport?.junitErrorsTeacher?.let { ttErr ->
                collapsibleGroup("Teacher Units Tests Errors") {
                    ttErr.split("ERROR: |FAILURE:".toRegex()).forEach { error ->
                        if (error.isNotBlank()) {
                            group {
                                row {
                                    val errorText = error.replace("<", "&lt;").replace("\n", "<br>")
                                    actionsButton(CopyAction(errorText))
                                    text(
                                        errorText
                                    )

                                }
                            }
                        }
                    }
                }
            }
            //SUMMARY
            buildReport.buildReport?.junitSummaryTeacher?.let { summary ->
                group {
                    row {
                        text("$summary<br>")
                    }
                }
            }


        }
        val scrollPane = JBScrollPane(panel)
        val viewport: JViewport = scrollPane.viewport
        viewport.scrollMode = JViewport.SIMPLE_SCROLL_MODE
        scrollPane.horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        viewport.extentSize = Dimension(0, 0)

        return scrollPane
    }

    /**
     * Puts each structure error back together with the lines that belong to it.
     *
     * The server reports a structure error as several entries: the message, and then one indented line for
     * each detail it names, e.g. "the pom.xml is missing the parent element" followed by the parent that was
     * expected. Read as a flat list, those details look like errors of their own, and a single problem is
     * shown as four.
     *
     * @return one list per error, holding its message and then its details
     */
    private fun groupStructureErrors(structureErrors: List<String>): List<List<String>> {
        val errors = mutableListOf<MutableList<String>>()
        for (line in structureErrors) {
            val isDetail = line.isNotEmpty() && line.first().isWhitespace()
            if (isDetail && errors.isNotEmpty()) {
                errors.last().add(line.trim())
            } else {
                errors.add(mutableListOf(line.trim()))
            }
        }
        return errors
    }

    inner class CopyAction(private val textToCopy: String) :
        DumbAwareAction("Copy Error", "Copy error", AllIcons.Actions.Copy) {
        override fun actionPerformed(e: AnActionEvent) {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(
                StringSelection(
                    textToCopy
                ), null
            )
        }
    }
}