package org.dropProject.dropProjectPlugin


import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import net.lingala.zip4j.ZipFile
import java.io.File
import javax.swing.JOptionPane

class ZipFolder(private val students: ArrayList<User>) {

    fun zipIt(e: AnActionEvent): String? {
        val projectDirectory = e.project?.let { FileEditorManager.getInstance(it).project.basePath.toString() }
        val separator = File.separator
        val newUploadFile = File("$projectDirectory${separator}projeto.zip")
        val authorsPath = "$projectDirectory${separator}AUTHORS.txt"
        val readMeTxtPath = "$projectDirectory${separator}README.txt"
        val readMeMdPath = "$projectDirectory${separator}README.md"
        val srcPath = "$projectDirectory${separator}src"
        val testsFilesPath = "$projectDirectory${separator}test-files"

        if (!File(authorsPath).exists()) {
            AuthorsFile(students).make(projectDirectory, true, e)
        }

        // Add AUTHORS.txt to a new zip
        ZipFile(newUploadFile)
            .addFile(File(authorsPath))

        return if (!File(srcPath).exists()) {
            JOptionPane.showMessageDialog(
                null, "Src Folder Not Found",
                "Submit Error",
                JOptionPane.ERROR_MESSAGE
            )
            null
        } else {

            val zipFile = ZipFile(newUploadFile)

            zipFile.addFolder(File(srcPath))

            if (File(testsFilesPath).exists()) {
                // Add the "test-files" folder on the existing zip
                zipFile.addFolder(File(testsFilesPath))
            }

            if (File(readMeTxtPath).exists()) {
                zipFile.addFile(File(readMeTxtPath))
            }

            if (File(readMeMdPath).exists()) {
                zipFile.addFile(File(readMeMdPath))
            }

            newUploadFile.path
        }


    }


}
