package com.tfc.ulht.dropProjectPlugin.toolWindow.panel

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.ui.components.panels.NonOpaquePanel
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.RefreshList
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.SubmitAssignment
import com.tfc.ulht.dropProjectPlugin.loginComponents.Logout
import com.tfc.ulht.dropProjectPlugin.loginComponents.MainLogin
import java.awt.FlowLayout
import javax.swing.BorderFactory

class ToolbarPanel() : NonOpaquePanel() {

    companion object {
        lateinit var toolbar:ActionToolbar
    }

    private var actionGroup:DefaultActionGroup
    init {

        layout = FlowLayout(FlowLayout.LEFT)
        border = BorderFactory.createEmptyBorder()

        actionGroup = DefaultActionGroup()
        toolbar = this.createToolbar()
        toolbar.targetComponent = this
        add(toolbar.component)
    }



     private fun createToolbar(): ActionToolbar {

        actionGroup.add(MainLogin())
        actionGroup.add(SubmitAssignment())
        actionGroup.add(Logout())
        actionGroup.addSeparator()
        actionGroup.add(RefreshList())

        //actionGroup.add(/*action2*/)
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
    }


}