package com.tfc.ulht.dropProjectPlugin

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.openapi.project.Project
import java.io.File
import javax.xml.bind.annotation.XmlRootElement


@XmlRootElement
data class Components(var selectedAssignmentID: String? = null)
class ProjectComponents(private val project: Project) {

    fun saveProjectComponents(assignmentId: String) {
        val components = Components(assignmentId)
        val metadataDir = File(File(project.basePath!!), ".dp")
        metadataDir.mkdirs()
        val metadataFile = File(metadataDir, "components.xml")
        try {
            val xmlMapper = XmlMapper().registerKotlinModule()
            xmlMapper.writeValue(metadataFile, components)
        } catch (_: Exception) {
        }

    }

    fun loadProjectComponents(): Components {
        val metadataDir = File(File(project.basePath!!), ".dp")
        val metadataFile = File(metadataDir, "components.xml")
        metadataDir.mkdirs()
        if (!metadataFile.isFile) {
            metadataFile.createNewFile()
        }
        return try {
            val xmlMapper = XmlMapper().registerKotlinModule()
            xmlMapper.readValue<Components>(metadataFile)
        } catch (_: Exception) {
            Components()
        }

    }

}