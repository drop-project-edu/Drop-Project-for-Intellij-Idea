package com.tfc.ulht.dropProjectPlugin.toolWindow.panel

import AssignmentTableModel
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.ui.components.panels.NonOpaquePanel
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.ListTable
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.SubmitAssignment
import com.tfc.ulht.dropProjectPlugin.loginComponents.Logout
import com.tfc.ulht.dropProjectPlugin.loginComponents.MainLogin
import java.awt.FlowLayout
import javax.swing.BorderFactory

class ToolbarPanel(private val tabelModel:AssignmentTableModel, private val resultsTable: ListTable) : NonOpaquePanel() {

    init {

        layout = FlowLayout(FlowLayout.LEFT)
        border = BorderFactory.createEmptyBorder()

        val toolbar : ActionToolbar = this.createToolbar()
        toolbar.targetComponent = this
        add(toolbar.component)
    }

     fun createToolbar(): ActionToolbar {
        val actionGroup = DefaultActionGroup()

        actionGroup.add(MainLogin(tabelModel, resultsTable))
        actionGroup.add(SubmitAssignment())
        actionGroup.add(Logout(tabelModel))




        //actionGroup.add(/*action2*/)
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
    }


}