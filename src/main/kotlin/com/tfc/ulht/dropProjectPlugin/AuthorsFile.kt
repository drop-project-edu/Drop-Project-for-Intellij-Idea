package com.tfc.ulht.dropProjectPlugin


import com.intellij.openapi.actionSystem.AnActionEvent
import com.tfc.ulht.dropProjectPlugin.loginComponents.LoginDialog.Companion.studentsList
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class AuthorsFile {

    fun make(projectDirectory: String?, silentMode : Boolean, e : AnActionEvent) {


        val authors = File("$projectDirectory/AUTHORS.txt")

        if (authors.delete()) {
            if (!silentMode)
                DefaultNotification.notify(e.project,"AUTHORS file was successfully modified")
        }
        else {
            if (!silentMode)
                DefaultNotification.notify(e.project,"AUTHORS file was successfully created")
        }

        val writer = BufferedWriter(FileWriter("$projectDirectory/AUTHORS.txt"))
        for (students in studentsList) {
            writer.write(students.toString())
            writer.newLine()

        }
        writer.close()

    }

}


