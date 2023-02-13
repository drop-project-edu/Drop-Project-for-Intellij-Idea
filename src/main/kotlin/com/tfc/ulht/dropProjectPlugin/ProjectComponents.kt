package com.tfc.ulht.dropProjectPlugin

import com.intellij.openapi.project.ProjectManager
import java.io.File
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlRootElement


@XmlRootElement
data class Components(var selectedAssignmentID: String? = null)
class ProjectComponents{

    fun saveProjectComponents(assignmentId: String){
        val components = Components(assignmentId)
        val project = ProjectManager.getInstance().openProjects[0]
        val metadataDir = File(File(project.basePath!!),".dp")
        metadataDir.mkdirs()
        val metadataFile = File(metadataDir,"components.xml")
        try {
            val context = JAXBContext.newInstance(Components::class.java)
            val marshaller = context.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true)
            marshaller.marshal(components,metadataFile)
        } catch (_: JAXBException){}

    }

    fun loadProjectComponents(): Components {
        val project = ProjectManager.getInstance().openProjects[0]
        val metadataDir = File(File(project.basePath!!),".dp")
        val metadataFile = File(metadataDir,"components.xml")
        metadataDir.mkdirs()
        if (!metadataFile.isFile){
            metadataFile.createNewFile()
        }
        return try {
            val context = JAXBContext.newInstance(Components::class.java)
            val unmarshaller = context.createUnmarshaller()
            unmarshaller.unmarshal(metadataFile) as Components
        } catch (_: JAXBException){
            Components()
        }

    }

}