package org.dropProject.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.dropProject.dropProjectPlugin.DefaultNotification
import org.dropProject.dropProjectPlugin.ProjectComponents
import org.dropProject.dropProjectPlugin.assignmentComponents.AssignmentTableLine
import org.dropProject.dropProjectPlugin.settings.SettingsState
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import javax.swing.JOptionPane

class ForgetAssignment(
    private val selectedTableLine: AssignmentTableLine,
    private val toolWindow: DropProjectToolWindow
) :
    DumbAwareAction("Forget Assignment", "Forget assignment", AllIcons.Actions.GC) {
    override fun actionPerformed(e: AnActionEvent) {
        val state = SettingsState.getInstance()
        if (state.isPublicAssignment(selectedTableLine.id_notVisible)) {
            if (selectedTableLine.radioButton.isSelected) {
                toolWindow.globals.selectedLine = null
            }
            state.removePublicAssignment(selectedTableLine.id_notVisible)
            ProjectComponents(toolWindow.project).saveProjectComponents("")
            toolWindow.updateAssignmentList()
            DefaultNotification.notify(toolWindow.project, "Assignment deleted successfully")
        } else {
            JOptionPane.showMessageDialog(
                null, "It is only possible to forget public assignments", "Private Assignment",
                JOptionPane.ERROR_MESSAGE
            )
        }

    }
}