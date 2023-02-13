package com.tfc.ulht.dropProjectPlugin.toolWindow.panel

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.ui.components.panels.NonOpaquePanel
import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.AddAssignment
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.CheckLastReport
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.RefreshList
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.SubmitAssignment
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import com.tfc.ulht.dropProjectPlugin.loginComponents.Logout
import com.tfc.ulht.dropProjectPlugin.loginComponents.MainLogin
import java.awt.BorderLayout
import javax.swing.BorderFactory

class ToolbarPanel() : NonOpaquePanel() {

    companion object {
        lateinit var toolbar:ActionToolbar
        lateinit var leftActionGroup:DefaultActionGroup
        lateinit var rightActionGroup:DefaultActionGroup
        fun loggedInToolbar(){

            leftActionGroup.removeAll()
            rightActionGroup.removeAll()

            leftActionGroup.add(AddAssignment())
            leftActionGroup.addSeparator()
            leftActionGroup.add(SubmitAssignment())
            leftActionGroup.addSeparator()
            leftActionGroup.add(RefreshList())

            rightActionGroup.add(Logout())
        }
        fun loggedOutToolbar(){
            leftActionGroup.removeAll()
            rightActionGroup.removeAll()
            leftActionGroup.add(MainLogin())
        }
        fun buildReportAvailable(){

            rightActionGroup.removeAll()

            rightActionGroup.add(CheckLastReport())
            rightActionGroup.addSeparator()
            rightActionGroup.add(Logout())
        }
    }


    init {

        layout = BorderLayout()//FlowLayout(FlowLayout.RIGHT)
        border = BorderFactory.createEmptyBorder()

        leftActionGroup = DefaultActionGroup()
        toolbar = this.createLeftToolbar()
        toolbar.targetComponent = this
        add(toolbar.component,BorderLayout.WEST)


        rightActionGroup = DefaultActionGroup()
        toolbar = this.createRightToolbar()
        toolbar.targetComponent = this
        add(toolbar.component,BorderLayout.EAST)

    }



     private fun createLeftToolbar(): ActionToolbar {
         if (Authentication.alreadyLoggedIn){
             leftActionGroup.add(AddAssignment())
             leftActionGroup.addSeparator()
             leftActionGroup.add(SubmitAssignment())
             leftActionGroup.addSeparator()
             leftActionGroup.add(RefreshList())
         } else{
             leftActionGroup.add(MainLogin())
         }

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, leftActionGroup, true)
    }
    private fun createRightToolbar(): ActionToolbar {
        if (Authentication.alreadyLoggedIn) {
            if (Globals.lastBuildReport != null) {
                rightActionGroup.add(CheckLastReport())
                rightActionGroup.addSeparator()
                rightActionGroup.add(Logout())
            } else {
                rightActionGroup.add(Logout())
            }

        }
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, rightActionGroup, true)
    }




}