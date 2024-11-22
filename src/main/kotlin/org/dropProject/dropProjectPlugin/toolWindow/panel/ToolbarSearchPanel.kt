package org.dropProject.dropProjectPlugin.toolWindow.panel

import com.intellij.openapi.actionSystem.*
import com.intellij.ui.JBColor
import com.intellij.ui.SearchTextField
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.panels.NonOpaquePanel
import org.dropProject.dropProjectPlugin.actions.GoBack
import org.dropProject.dropProjectPlugin.actions.PanelRoute
import org.dropProject.dropProjectPlugin.actions.SearchAssignment
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.awt.FlowLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BorderFactory

class ToolbarSearchPanel(val e: AnActionEvent, private val toolWindow: DropProjectToolWindow) :
    NonOpaquePanel() {

    private var assignmentSearchField: SearchTextField
    private var leftActionGroup: DefaultActionGroup
    private var rightActionGroup: DefaultActionGroup

    init {

        layout = FlowLayout(FlowLayout.LEFT)
        border = BorderFactory.createEmptyBorder()

        leftActionGroup = DefaultActionGroup()
        var toolbar = this.createLeftToolbar()
        toolbar.targetComponent = this
        add(toolbar.component)

        assignmentSearchField = SearchTextField()
        assignmentSearchField.textEditor.emptyText.appendText(
            "Assignment ID",
            SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY)
        )
        assignmentSearchField.addKeyboardListener(keyAdapter(e))
        add(assignmentSearchField)

        rightActionGroup = DefaultActionGroup()
        toolbar = this.createRightToolbar()
        toolbar.targetComponent = this
        add(toolbar.component)

    }

    private fun keyAdapter(event: AnActionEvent): KeyAdapter {
        return object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (KeyEvent.VK_ENTER == e.keyCode) {
                    SearchAssignment(
                        assignmentIDField = assignmentSearchField,
                        toolWindow = toolWindow,
                        route = PanelRoute.SEARCH,
                        selectAssignment = true
                    ).actionPerformed(event)
                }
            }
        }
    }

    fun clearSearchText() {
        assignmentSearchField.text = ""
    }

    private fun createLeftToolbar(): ActionToolbar {
        leftActionGroup.add(GoBack(toolWindow))
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, leftActionGroup, true)
    }

    private fun createRightToolbar(): ActionToolbar {
        rightActionGroup.add(
            SearchAssignment(
                assignmentIDField = assignmentSearchField,
                toolWindow = toolWindow,
                route = PanelRoute.SEARCH,
                selectAssignment = true
            )
        )
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, rightActionGroup, true)
    }


}

