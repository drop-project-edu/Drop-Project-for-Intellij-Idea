package com.tfc.ulht.dropProjectPlugin


import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import net.lingala.zip4j.ZipFile
import java.io.File
import javax.swing.JOptionPane


/*-
 * Plugin Drop Project
 * 
 * Copyright (C) 2019 Yash Jahit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class ZipFolder {

    fun zipIt(e: AnActionEvent): String? {
        val projectDirectory = e.project?.let { FileEditorManager.getInstance(it).project.basePath.toString() }

        val newUploadFile = File("$projectDirectory\\projeto.zip")
        val authorsPath = "$projectDirectory\\AUTHORS.txt"
        val srcPath = "$projectDirectory\\src"

        if (!File(authorsPath).exists()){
            AuthorsFile().make(projectDirectory,true,e)
        }

        // Add AUTHORS.txt to a new zip
        ZipFile(newUploadFile)
            .addFile(File(authorsPath))

        if (!File(srcPath).exists()) {
            JOptionPane.showMessageDialog(
                null, "Src Folder Not Found",
                "Submit Error",
                JOptionPane.ERROR_MESSAGE
            )
            return null
        }
        else {
            // Add the "src" folder on the existing zip
            ZipFile(newUploadFile)
                .addFolder(File(srcPath))

            return newUploadFile.path
        }


    }


}
