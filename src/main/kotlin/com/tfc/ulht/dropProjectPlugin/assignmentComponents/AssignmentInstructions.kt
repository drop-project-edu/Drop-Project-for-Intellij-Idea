package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.VirtualFile
import data.AssignmentInstructionsFormat
import javax.swing.JEditorPane
import javax.swing.event.HyperlinkEvent

class AssignmentInstructions(
    private val assignmentID: String,
    private val format: AssignmentInstructionsFormat?,
    private val body: String?
) {
    private val fontColor = "#317092"
    val backgroundColor = "white"
    private val foregroundColor = "#e0ffff"

    fun showInstructions(project: Project?) {
        val html = buildHtml()
        if (format == AssignmentInstructionsFormat.HTML) {
            val editor = JEditorPane("text/html", html) // PREPARE HTML VIEWER
            editor.isEditable = false
            // ABILITY TO CLICK ON HYPERLINK
            editor.addHyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {

                    BrowserUtil.browse(e.url.toURI())
                }
            }
            editor.caretPosition = 0
            editor.margin = JBUI.insets(0, 10, 10, 10)
            val jbScrollPane = JBScrollPane(editor)
            val editorManager = project?.let { FileEditorManager.getInstance(it) }
            val virtualFile = VirtualFile("Assignment Details", jbScrollPane)
            editorManager?.openFile(virtualFile, true)
        } else {
            Messages.showMessageDialog("Unrecognized instructions format", "Format Error", Messages.getErrorIcon())
        }
    }

    private fun buildHtml(): String {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                instructionsStyle() +
                "</head>" +
                "<body>" +
                body +
                linkButton() +
                "</body>" +
                "</html>"
    }

    private fun instructionsStyle(): String {
        return "<style>" +
                "body {font-family: arial, sans-serif;padding:10px;background-color:$backgroundColor;color:$fontColor;max-width: 760px; margin: auto;}" +
                "h2 {font-size:1.4em}" +
                "h3 {font-size:1.2em}" +
                "code {color: red;font-size:1em;background-color:$backgroundColor;padding:3px 6px;}" +
                "ul {background-color:$foregroundColor;color:$fontColor;padding: 8px 20px;margin:auto;}" +
                "li {padding: 5px 0px;}" +
                "</style>"

    }

    private fun linkButton(): String {
        return "<br><br><div style = \"cursor:pointer;padding:10px 20px;background-color:$foregroundColor;text-align:center;\"><a style=\"text-decoration:none;color:$fontColor;font-size:1.3em;\" href=\"${Globals.REQUEST_URL}/upload/${assignmentID}\">Instructions in Web \uD83D\uDD17</a></div>"
    }

}