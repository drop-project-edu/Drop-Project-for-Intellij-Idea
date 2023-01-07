package com.tfc.ulht.dropProjectPlugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@com.intellij.openapi.components.Service
@State(name = "ProjectComponents", storages = [Storage("ProjectComponent.xml")])
class ProjectComponents : PersistentStateComponent<ProjectComponents.State>{

    private var myState = State()
    //private var selectedAssignmentID: String? = null

    data class State(var selectedAssignmentID: String? = null)

    fun getProjectSelectedAssignmentID(): String? {
        return myState.selectedAssignmentID
    }

    fun setProjectSelectedAssignmentID(assignmentID: String) {
        myState.selectedAssignmentID = assignmentID
    }

    override fun getState(): ProjectComponents.State {
        return myState
    }

    override fun loadState(state: ProjectComponents.State) {
        myState = state
    }

}