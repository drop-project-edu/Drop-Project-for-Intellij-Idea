/*
 * Created by JFormDesigner on Tue Nov 22 17:28:27 WET 2022
 */
package com.tfc.ulht.dropProjectPlugin.submissionTable

import com.intellij.ui.components.JBScrollPane
import data.FullBuildReport
import net.miginfocom.swing.MigLayout
import java.awt.Font
import javax.swing.*

/**
 * @author bernardobaltazar
 */
class buildreportUI(fullBuildReport: FullBuildReport) : JPanel() {
    init {
        initComponents(fullBuildReport)
    }

    private fun initComponents(fullBuildReport: FullBuildReport) {

        var passedAllTests = false

        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        val label1 = JLabel()
        val separator1 = JSeparator()
        val assignmentLabel = JLabel()
        val packageLabel = JLabel()
        val submissionDateLabel = JLabel()
        val separator2 = JSeparator()
        val resultsSummaryLabel = JLabel()
        val separator3 = JSeparator()
        val psLabel = JLabel()
        val psResponse = JLabel()
        val separator4 = JSeparator()
        val cLabel = JLabel()
        val cResponse = JLabel()
        val separator5 = JSeparator()
        val ccLabel = JLabel()
        val ccResponse = JLabel()
        val separator7 = JSeparator()
        val ttLabel = JLabel()
        val ttResponse = JLabel()
        val separator8 = JSeparator()
        val jUnitSummaryLabel = JLabel()
        val separator9 = JSeparator()
        val jUnitSummaryTeacherLabel = JLabel()
        val jUnitErrors = JBScrollPane()


        //======== this ========
        layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]",  // rows
            "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]"
        )

        //---- label1 ----
        label1.icon = ImageIcon(ImageIcon(this.javaClass.classLoader.getResource("images/report-file.png")).image)
        label1.text = "Build Report"
        label1.font = Font("Segoe UI", Font.BOLD, 24)
        add(label1, "cell 0 0,width 30,height 30")
        add(separator1, "cell 0 1 14 1")

        //---- assignementLabel ----
        assignmentLabel.text = "Assignment: " + fullBuildReport.assignment?.name
        assignmentLabel.font = Font("Segoe UI", Font.PLAIN, 12)
        add(assignmentLabel, "cell 0 2,width 30,height 30")

        //---- PackageLabel ----
        if (fullBuildReport.assignment?.packageName != null || fullBuildReport.assignment?.packageName != "" ){
            packageLabel.text = "Package: " + fullBuildReport.assignment?.packageName
        }
        else {
            packageLabel.text = "Package: None"
        }
        packageLabel.font = Font("Segoe UI", Font.PLAIN, 12)
        add(packageLabel, "cell 0 3,width 30,height 30")


        //---- SubmissionDate ---
        val date = (fullBuildReport.submission?.submissionDate.toString()).split(".")[0].split("T")

        submissionDateLabel.text = "Submission Date: " + date[0] + " Hour: " + date[1]
        submissionDateLabel.font = Font("Segoe UI", Font.PLAIN, 12)
        add(submissionDateLabel, "cell 0 4,width 30,height 30")
        add(separator2, "cell 0 5 14 1")


        //---- ResultsSummaryLabel ----
        resultsSummaryLabel.text = "Results Summary"
        resultsSummaryLabel.font = Font("Segoe UI", Font.BOLD, 24)
        add(resultsSummaryLabel, "cell 0 7,width 30,height 30")
        add(separator3, "cell 0 8 14 1")


        for (summary in fullBuildReport.summary!!) {
            when (summary.reportKey) {
                "PS" ->  if (summary.reportValue=="OK"){
                    psResponse.text = "✅"
                } else {
                    psResponse.text = "❌"
                }
                "C" -> if (summary.reportValue=="OK"){
                    cResponse.text = "✅"
                } else {
                    cResponse.text = "❌"
                }
                "CS" -> if (summary.reportValue=="OK"){
                    ccResponse.text = "✅"
                } else {
                    ccResponse.text = "❌"
                }
                "TT" -> if (summary.reportValue=="OK"){
                    passedAllTests = true
                    ttResponse.text = "✅ ${summary.reportProgress}/${summary.reportGoal}"
                } else {
                    ttResponse.text = "❌ ${summary.reportProgress}/${summary.reportGoal}"
                }
            }
        }

        //---- PSLabel ----
        psLabel.text = "Project Structure"
        psLabel.font = Font("Segoe UI", Font.PLAIN, 18)
        add(psLabel, "cell 0 9,width 30,height 30")

        //---- PSResponse ----
        psResponse.font = Font("", Font.PLAIN, 18)
        add(psResponse, "cell 1 9")
        add(separator4, "cell 0 10 3 1")

        //---- CLabel ----
        cLabel.text = "Compilation"
        cLabel.font = Font("Segoe UI", Font.PLAIN, 18)
        add(cLabel, "cell 0 11,width 30,height 30")

        //---- CResponse ----
        cResponse.font = Font("", Font.PLAIN, 18)
        add(cResponse, "cell 1 11")
        add(separator5, "cell 0 12 3 1")

        //---- CCLabel ----
        ccLabel.text = "Code Quality (Checkstyle)"
        ccLabel.font = Font("Segoe UI", Font.PLAIN, 18)
        add(ccLabel, "cell 0 13,width 30,height 30")

        //---- CCResponse ----
        ccResponse.font = Font("", Font.PLAIN, 18)
        add(ccResponse, "cell 1 13")
        add(separator7, "cell 0 14 3 1")

        //---- TTLabel ----
        ttLabel.text = "Teacher Unit Tests"
        ttLabel.font = Font("Segoe UI", Font.PLAIN, 18)
        add(ttLabel, "cell 0 15,width 30,height 30")

        //---- TTResponse ----
        ttResponse.font = Font("", Font.PLAIN, 18)
        add(ttResponse, "cell 1 15")
        add(separator8, "cell 0 16 3 1")

        //-----StructErr or BuildSummary
        if (fullBuildReport.structureErrors != null) {
            jUnitSummaryLabel.text = "Structure Errors"
        } else if (fullBuildReport.buildReport?.compilationErrors !=null){
            jUnitSummaryLabel.text = "Compilation Errors"
        } else if(fullBuildReport.buildReport?.checkstyleErrors !=null) {
            jUnitSummaryLabel.text = "Checkstyle Errors"
        } else {
            jUnitSummaryLabel.text = "JUnit Summary"
        }

        //---- JUnitSummaryLabel ----
        jUnitSummaryLabel.font = Font("Segoe UI", Font.BOLD, 18)
        add(jUnitSummaryLabel, "cell 0 18,width 30,height 30")
        add(separator9, "cell 0 19 14 1")

        //---- jUnitSummaryTeacherLabel ----
        if (fullBuildReport.structureErrors != null) {
            jUnitSummaryTeacherLabel.text = "You're code has ${fullBuildReport.structureErrors.size} structure error${if (fullBuildReport.structureErrors.size!=1){"s"} else {
                ""
            }
            }"
        } else if (fullBuildReport.buildReport?.compilationErrors !=null){
            val size = fullBuildReport.buildReport.compilationErrors.filter { it.startsWith("[TEST]") }.size
            jUnitSummaryTeacherLabel.text = "You're code has $size compilation error${if (size!=1){"s"} else {
                ""
            }
            }"
        } else if(fullBuildReport.buildReport?.checkstyleErrors !=null) {
            val size = fullBuildReport.buildReport.checkstyleErrors.size
            jUnitSummaryTeacherLabel.text = "You're code has ${size} code quality error${if (size!=1){"s"} else {
                ""
            }
            }"
        } else {
            jUnitSummaryTeacherLabel.text = fullBuildReport.buildReport?.junitSummaryTeacher
        }

        //---- jUnitSummaryTeacherLabel ----
        jUnitSummaryTeacherLabel.font = Font("Segoe UI", Font.BOLD, 14)
        add(jUnitSummaryTeacherLabel, "cell 0 20,width 30,height 30")
        //add(separator10, "cell 0 21 14 1")

        //======== jUnitErrors ========

        //---- textArea1 ----
        if (fullBuildReport.structureErrors == null) {

            if (!passedAllTests){
                var cellNum = 22
                if (fullBuildReport.buildReport?.compilationErrors !=null){

                    for (cError in fullBuildReport.buildReport.compilationErrors){
                        val errorLabel = JLabel("<html><p>${cError}<p/></html>")

                        errorLabel.font = Font("Segoe UI", Font.ITALIC, 12)
                        if (cError.startsWith("[TEST]")){
                            val separator = JSeparator()
                            add(separator, "cell 0 ${cellNum++} 3 1")
                        }
                        add(errorLabel, "cell 0 ${cellNum++},width 30,height 30")

                    }
                } else if (fullBuildReport.buildReport?.checkstyleErrors !=null){

                    for (ccError in fullBuildReport.buildReport.checkstyleErrors){
                        val errorLabel = JLabel(ccError)
                        val separator = JSeparator()
                        errorLabel.font = Font("Segoe UI", Font.ITALIC, 12)
                        add(errorLabel, "cell 0 ${cellNum++},width 30,height 30")
                        add(separator, "cell 0 ${cellNum++} 3 1")
                    }
                } else if(fullBuildReport.buildReport?.junitErrorsTeacher !=null){
                    var array:ArrayList<String> = arrayListOf()
                    val iterator = "ERROR:|FAILURE:".toRegex().findAll(fullBuildReport.buildReport.junitErrorsTeacher).iterator()
                    while(iterator.hasNext()){
                        array.add(iterator.next().value)
                    }
                    var index = 0
                    for (jError in fullBuildReport.buildReport.junitErrorsTeacher.split("ERROR:|FAILURE:".toRegex())) {
                        if (jError.isNotBlank()){
                            val label = JLabel(array[index++])
                            val errorLabel = JLabel("<html><p>${jError}<p/></html>")
                            errorLabel.font = Font("Segoe UI", Font.ITALIC, 12)
                            val separator = JSeparator()
                            val separator2 =JSeparator()
                            add(separator, "cell 0 ${cellNum++} 3 1")
                            add(label, "cell 0 ${cellNum++},width 30,height 30")
                            add(separator2, "cell 0 ${cellNum++} 3 1")
                            add(errorLabel, "cell 0 ${cellNum++},width 30,height 30")
                        }
                    }
                }
            }



        } else {
            var cellNum = 22
            for (error in fullBuildReport.structureErrors) {
                val errorLabel = JLabel(error)
                val separator = JSeparator()
                errorLabel.font = Font("Segoe UI", Font.ITALIC, 12)
                add(errorLabel, "cell 0 ${cellNum++},width 30,height 30")
                add(separator, "cell 0 ${cellNum++} 3 1")
            }
        }


        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off



}