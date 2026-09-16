package org.dropProject.dropProjectPlugin


import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import data.SubmissionStructure
import net.lingala.zip4j.ZipFile
import java.io.File
import javax.swing.JOptionPane

/**
 * Builds the zip that is submitted, with the contents that the assignment's [submissionStructure] calls for.
 *
 * A compact submission carries the sources and the test-files folder, and the server builds the maven project
 * around them. A maven submission carries the project the student wrote, so it has to include its pom.xml;
 * test-files has no place in it, since a maven project keeps that kind of file in src/main/resources and
 * src/test/resources, which are already inside src.
 */
class ZipFolder(
    private val students: ArrayList<User>,
    private val submissionStructure: SubmissionStructure = SubmissionStructure.COMPACT
) {

    fun zipIt(e: AnActionEvent): String? {
        val projectDirectory = e.project?.let { FileEditorManager.getInstance(it).project.basePath.toString() }
        val separator = File.separator
        val newUploadFile = File("$projectDirectory${separator}projeto.zip")
        val authorsPath = "$projectDirectory${separator}AUTHORS.txt"
        val readMeTxtPath = "$projectDirectory${separator}README.txt"
        val readMeMdPath = "$projectDirectory${separator}README.md"
        val srcPath = "$projectDirectory${separator}src"
        val testsFilesPath = "$projectDirectory${separator}test-files"
        val pomPath = "$projectDirectory${separator}pom.xml"

        if (!File(srcPath).exists()) {
            showError("Src Folder Not Found")
            return null
        }

        // a maven submission that does not carry the pom.xml is refused by the server, so the student is
        // told here, before anything is uploaded and before they wait for a build that cannot happen
        if (submissionStructure == SubmissionStructure.MAVEN && !File(pomPath).exists()) {
            showError(
                "This assignment expects a Maven project, and there is no pom.xml in the root of this project."
            )
            return null
        }

        if (!File(authorsPath).exists()) {
            AuthorsFile(students).make(projectDirectory, true, e)
        }

        // Add AUTHORS.txt to a new zip
        ZipFile(newUploadFile)
            .addFile(File(authorsPath))

        val zipFile = ZipFile(newUploadFile)

        zipFile.addFolder(File(srcPath))

        if (submissionStructure == SubmissionStructure.MAVEN) {
            zipFile.addFile(File(pomPath))
        } else if (File(testsFilesPath).exists()) {
            // Add the "test-files" folder on the existing zip
            zipFile.addFolder(File(testsFilesPath))
        }

        if (File(readMeTxtPath).exists()) {
            zipFile.addFile(File(readMeTxtPath))
        }

        if (File(readMeMdPath).exists()) {
            zipFile.addFile(File(readMeMdPath))
        }

        return newUploadFile.path
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(null, message, "Submit Error", JOptionPane.ERROR_MESSAGE)
    }
}
