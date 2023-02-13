package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.tfc.ulht.dropProjectPlugin.Globals
import data.AssignmentInstructionsFormat
import java.awt.BorderLayout
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.event.HyperlinkEvent

class AssignmentInstructions(private val assignmentID: String, private val format: AssignmentInstructionsFormat?, private val body: String?){

    fun showInstructions(project: Project?) {
        if (format == AssignmentInstructionsFormat.HTML){

            val editor = JEditorPane("text/html", body) // PREPARE HTML VIEWER
            editor.isEditable = false
            // ABILITY TO CLICK ON HYPERLINK
            editor.addHyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {

                    BrowserUtil.browse(e.url.toURI())
                }
            }
            editor.caretPosition = 0
            editor.margin = JBUI.insets(0,10,10,10)
            val jbScrollPane = JBScrollPane(editor)
            val linkPane = JEditorPane("text/html", " <a style=\"text-decoration:none;color:#D4D4D3\" href=\"${Globals.REQUEST_URL}/upload/${assignmentID}\">Instructions in Web \uD83D\uDD17</a>")
            linkPane.isEditable = false
            linkPane.margin = JBUI.insets(10)
            linkPane.addHyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {

                    BrowserUtil.browse(e.url.toURI())
                }
            }
            val header = JPanel(BorderLayout())
            val title = JEditorPane("text/html","<b>Assignment Instructions<b/>")
            title.isEditable = false
            title.margin = JBUI.insets(10)
            header.add(linkPane,BorderLayout.EAST)
            header.add(title,BorderLayout.WEST)

            val panel = JPanel(BorderLayout())
            panel.add(jbScrollPane,BorderLayout.CENTER)
            panel.add(header,BorderLayout.NORTH)
            val editorManager = project?.let { FileEditorManager.getInstance(it) }
            val virtualFile = InstructionsVirtualFile("Assignment Details", panel)
            editorManager?.openFile(virtualFile,true)
        } else {
            Messages.showMessageDialog("Unrecognized instructions format", "Format Error", Messages.getErrorIcon())
        }
    }

}