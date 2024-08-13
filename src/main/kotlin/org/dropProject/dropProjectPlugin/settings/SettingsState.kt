package org.dropProject.dropProjectPlugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "org.dropproject.dropProjectPlugin.SettingsState",
    storages = [Storage("DropProjectSettings.xml")]
)
class SettingsState : PersistentStateComponent<SettingsState> {

    var serverURL: String = ""
    var username: String = ""
    var usernumber: String = ""
    var token: String = ""
    var publicAssignments: MutableList<String> = mutableListOf()

    companion object {
        fun getInstance(): SettingsState {
            return ApplicationManager.getApplication().getService(SettingsState::class.java)
        }
    }

    override fun getState(): SettingsState {
        return this
    }

    override fun loadState(state: SettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun updateValues(serverUrl: String = "", userName: String = "", userNumber: String = "", token: String = "") {
        this.serverURL = serverUrl
        this.username = userName
        this.usernumber = userNumber
        this.token = token
    }

    fun addPublicAssignment(id: String) {
        publicAssignments.add(id)
    }

    fun removePublicAssignment(id: String) {
        publicAssignments.remove(id)
    }

    fun isPublicAssignment(id: String): Boolean {
        return publicAssignments.contains(id)
    }
}