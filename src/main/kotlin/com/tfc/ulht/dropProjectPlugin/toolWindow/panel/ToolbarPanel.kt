package com.tfc.ulht.dropProjectPlugin.toolWindow.panel

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.panels.NonOpaquePanel
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.RefreshList
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.SubmitAssignment
import com.tfc.ulht.dropProjectPlugin.assignmentComponents.checkLastReport
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import com.tfc.ulht.dropProjectPlugin.loginComponents.Logout
import com.tfc.ulht.dropProjectPlugin.loginComponents.MainLogin
import java.awt.FlowLayout
import javax.swing.BorderFactory

class ToolbarPanel() : NonOpaquePanel() {

    companion object {
        lateinit var toolbar:ActionToolbar
        private lateinit var actionGroup:DefaultActionGroup
        fun loggedInToolbar(){

            actionGroup.removeAll()
            actionGroup.add(Logout())
            actionGroup.addSeparator()
            actionGroup.add(SubmitAssignment())
            actionGroup.addSeparator()
            actionGroup.add(RefreshList())
        }
        fun loggedOutToolbar(){
            actionGroup.removeAll()
            actionGroup.add(MainLogin())
        }
        fun buildReportAvailable(){
            actionGroup.removeAll()
            actionGroup.add(Logout())
            actionGroup.addSeparator()
            actionGroup.add(SubmitAssignment())
            actionGroup.addSeparator()
            actionGroup.add(RefreshList())
            actionGroup.addSeparator()
            actionGroup.add(checkLastReport())
        }
    }


    init {

        layout = FlowLayout(FlowLayout.LEFT)
        border = BorderFactory.createEmptyBorder()

        actionGroup = DefaultActionGroup()
        toolbar = this.createToolbar()
        toolbar.targetComponent = this
        add(toolbar.component)
    }



     private fun createToolbar(): ActionToolbar {

         if (!Authentication.alreadyLoggedIn){
             actionGroup.add(MainLogin())
         } else {
             actionGroup.add(Logout())
             actionGroup.addSeparator()
             actionGroup.add(SubmitAssignment())
             actionGroup.addSeparator()
             actionGroup.add(RefreshList())
         }

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
    }




}