package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import okhttp3.Request
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.event.HyperlinkEvent

class AssignmentInstructions(private val assignmentID: String){


    private val REQUEST_URL = "${Globals.REQUEST_URL}/api/student/assignment"
    private var htmlFragment : String = ""
    /*private var CSS frag (idea)*/

    init {
        val request = Request.Builder()
            .url("$REQUEST_URL/$assignmentID/instructions")
            .build()

        Authentication.httpClient.newCall(request).execute().use { response ->

            htmlFragment = response.body!!.string()

        }

    }
     private fun getInstructionsFragment(): String {

        return htmlFragment
    }

    fun showInstructions(project: Project?) {




        val ed1 = JEditorPane("text/html", getInstructionsFragment()) // PREPARE HTML VIEWER
        ed1.isEditable = false
        // ABILITY TO CLICK ON HYPERLINK
        ed1.addHyperlinkListener { e ->
            if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {

                BrowserUtil.browse(e.url.toURI())
            }
        }
        ed1.caretPosition = 0
        ed1.margin = JBUI.insets(0,10,10,10)
        val jbScrollPane = JBScrollPane(ed1)
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
        /*val tempFrame = JFrame()
        tempFrame.add(panel)
        tempFrame.isVisible = true
        tempFrame.setSize(700, 600)*/
        val editorManager = project?.let { FileEditorManager.getInstance(it) }
        val virtualFile = InstructionsVirtualFile("Assignment Details", panel)
        editorManager?.openFile(virtualFile,true)



    }

}