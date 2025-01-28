package org.dropProject.dropProjectPlugin.assignmentComponents

import com.intellij.lang.documentation.DocumentationSettings
import com.intellij.openapi.editor.impl.EditorCssFontResolver
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.BrowserHyperlinkListener
import com.intellij.ui.ColorUtil
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.util.ui.*
import data.AssignmentInstructionsFormat
import org.dropProject.dropProjectPlugin.VirtualFile
import java.net.URL
import javax.swing.JEditorPane
import javax.swing.UIManager
import javax.swing.text.html.HTMLDocument

class AssignmentInstructions(
    private val requestUrl: String,
    private val assignmentID: String,
    private val format: AssignmentInstructionsFormat?,
    private val body: String?
) {
    fun showInstructions(project: Project?) {
        val html = buildHtml()
        if (format == AssignmentInstructionsFormat.HTML) {

            val customEditorKit = HTMLEditorKitBuilder()
                .withViewFactoryExtensions(
                    ExtendableHTMLViewFactory.Extensions.WORD_WRAP
                )
                .withFontResolver(EditorCssFontResolver.getGlobalInstance())
                .build()
                .also {
                    val baseFontSize = UIManager.getFont("Label.font").size
                    val codeFontName = EditorCssFontResolver.EDITOR_FONT_NAME_NO_LIGATURES_PLACEHOLDER

                    val paragraphSpacing = """padding: ${scale(4)}px 0 ${scale(4)}px 0"""

                    // Also, look at com.intellij.codeInsight.documentation.DocumentationHtmlUtil::getDocumentationPaneAdditionalCssRules
                    it.styleSheet.addStyleSheet(
                        StyleSheetUtil.loadStyleSheet(
                            """
                        h6 { font-size: ${baseFontSize + 1}}
                        h5 { font-size: ${baseFontSize + 2}}
                        h4 { font-size: ${baseFontSize + 3}}
                        h3 { font-size: ${baseFontSize + 6}}
                        h2 { font-size: ${baseFontSize + 8}}
                        h1 { font-size: ${baseFontSize + 12}}
                        h1, h2, h3, h4, h5, h6 {margin: 0 0 0 0; $paragraphSpacing; }
                        p { margin: 0 0 0 0; $paragraphSpacing; line-height: 125%; }
                        ul { margin: 0 0 0 ${scale(10)}px; $paragraphSpacing;}
                        ol { margin: 0 0 0 ${scale(20)}px; $paragraphSpacing;}
                        li { padding: ${scale(1)}px 0 ${scale(2)}px 0; }
                        li p { padding-top: 0; padding-bottom: 0; }
                        hr { 
                            padding: ${scale(1)}px 0 0 0; 
                            margin: ${scale(4)}px 0 ${scale(4)}px 0; 
                            border-bottom: ${scale(1)}px solid ${ColorUtil.toHtmlColor(UIUtil.getTooltipSeparatorColor())}; 
                            width: 100%;
                        }
                        code, pre, .pre { 
                            font-family:"$codeFontName"; 
                            color: ${ColorUtil.toHtmlColor(JBUI.CurrentTheme.Link.Foreground.ENABLED)}; 
                            padding: 2px 4px;
                        }
                        a { color: ${ColorUtil.toHtmlColor(JBUI.CurrentTheme.Link.Foreground.ENABLED)}; text-decoration: none; }
                        """.trimIndent()
                        )
                    )
                }

            val editor = JEditorPane().apply {
                background = UIUtil.getPanelBackground()
                addHyperlinkListener(BrowserHyperlinkListener.INSTANCE)
                isEditable = false
                editorKit = customEditorKit
                text = html
                isFocusable = true  // to allow copy&paste
            }

            val baseUrl = URL("$requestUrl/upload/")
            val doc = editor.document as HTMLDocument
            doc.base = baseUrl

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
                "<body>" +
                body +
                linkButton() +
                "</body>" +
                "</html>"
    }

    private fun linkButton(): String {
        return "<br><br><div style = \"cursor:pointer;padding:10px 20px;background-color:#317092;text-align:center;\"><a style=\"text-decoration:none;color:#e0ffff;font-size:1.3em;\" href=\"${requestUrl}/upload/${assignmentID}\">Instructions in Web \uD83D\uDD17</a></div>"
    }


}