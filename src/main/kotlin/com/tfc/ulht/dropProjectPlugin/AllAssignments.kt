package com.tfc.ulht.dropProjectPlugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.tfc.ulht.dropProjectPlugin.AllAssignments",
    storages = [Storage("DropProjectAllAssignments.xml")]
)
class AllAssignments : PersistentStateComponent<AllAssignments> {

    companion object {
        fun getInstance(): AllAssignments {
            return ApplicationManager.getApplication().getService(AllAssignments::class.java)
        }
    }

    private val listOfIds: ArrayList<String> = ArrayList()

    fun getListOfIds(): ArrayList<String> {
        return listOfIds
    }

    override fun getState(): AllAssignments {
        return this
    }

    override fun loadState(state: AllAssignments) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun addAssignmentID(id: String) {
        listOfIds.add(id)
    }

    fun removeAssignmentID(id: String): Boolean {
        return listOfIds.remove(id)
    }
}