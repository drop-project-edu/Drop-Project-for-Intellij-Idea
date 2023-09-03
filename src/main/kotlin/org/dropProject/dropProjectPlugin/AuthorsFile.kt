package org.dropProject.dropProjectPlugin


import com.intellij.openapi.actionSystem.AnActionEvent
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class AuthorsFile(private val students: ArrayList<User>) {

    fun make(projectDirectory: String?, silentMode: Boolean, e: AnActionEvent) {


        val authors = File("$projectDirectory${File.separator}AUTHORS.txt")

        if (authors.delete()) {
            if (!silentMode)
                DefaultNotification.notify(e.project, "AUTHORS file was successfully modified")
        } else {
            if (!silentMode)
                DefaultNotification.notify(e.project, "AUTHORS file was successfully created")
        }

        val writer = BufferedWriter(FileWriter("$projectDirectory${File.separator}AUTHORS.txt"))
        for (students in students) {
            writer.write(students.toString())
            writer.newLine()

        }
        writer.close()

    }

}


