package com.tfc.ulht.dropProjectPlugin.submissionComponents

import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolder
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBScrollPane
import com.tfc.ulht.dropProjectPlugin.submissionTable.buildreportUI
import data.FullBuildReport
import org.jetbrains.annotations.NotNull
import java.beans.PropertyChangeListener
import java.io.InputStream
import java.io.OutputStream
import javax.swing.JComponent

class BuildReportEditorTabProvider : FileEditorProvider, DumbAware {
    override fun accept(project: @NotNull Project, file: @NotNull VirtualFile): Boolean {
        return file is BuildReportVirtualFile
    }

    override fun createEditor(project: @NotNull Project, file: @NotNull VirtualFile): FileEditor {
        // Create and return a new instance of the custom FileEditor implementation
        return BuildReportPanelEditor(file as BuildReportVirtualFile)
    }

    override fun getEditorTypeId(): @NotNull String {
        return "build-report-editor"
    }

    override fun getPolicy(): @NotNull FileEditorPolicy {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR//HIDE_DEFAULT_EDITOR
    }
}

class BuildReportPanelEditor (private val myVirtualFile: BuildReportVirtualFile) : FileEditor, UserDataHolder {

    private var myPanel = JBScrollPane()
    private val userData = UserDataHolderBase()

    init {

        myPanel = JBScrollPane(buildreportUI(myVirtualFile.fullBuildReport))
    }
    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return userData.getUserData(key)
    }

    override fun getFile(): VirtualFile {
        return myVirtualFile
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        userData.putUserData(key,value)
    }

    override fun dispose() {
        //perform any cleanup when the editor is closed
    }

    override fun getComponent(): JComponent {
        return myPanel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        // Return the component that should receive focus when the editor tab is selected
        return myPanel
    }

    override fun getName(): String {
        return myVirtualFile.name
    }

    override fun getState(level: FileEditorStateLevel): FileEditorState {
        return FileEditorState.INSTANCE
    }
    override fun setState(state: FileEditorState) {
        //save the state of the editor if necessary
    }

    override fun isModified(): Boolean {
        //return true if the editor has unsaved changes, false otherwise
        return false
    }

    override fun isValid(): Boolean {
        //return true if the editor is in a valid state, false otherwise
        return true
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        //add a property change listener if necessary
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        //remove a property change listener if necessary
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

}
class BuildReportVirtualFile(private val myName : String, val fullBuildReport: FullBuildReport) : VirtualFile() {
    override fun getName(): String {
        return myName
    }

    override fun getFileSystem(): VirtualFileSystem {
        return BuildReportFileSystem.INSTANCE
    }

    override fun getPath(): String {
        return "/$myName"
    }

    override fun isWritable(): Boolean {
        return false
    }

    override fun isDirectory(): Boolean {
        // Return true if the virtual file is a directory, false otherwise
        return false
    }

    override fun isValid(): Boolean {
        // Return true if the virtual file is valid, false otherwise
        return true
    }

    override fun getParent(): VirtualFile? {
        // Return the parent of the virtual file, if any
        return null
    }

    override fun getChildren(): Array<VirtualFile>? {
        // Return the children of the virtual file, if any
        return null
    }

    override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        // Return an output stream for writing to the virtual file
        throw UnsupportedOperationException()
    }

    override fun contentsToByteArray(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getTimeStamp(): Long {
        // Return the timestamp of the virtual file
        return 0
    }

    override fun getLength(): Long {
        return 0
    }

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
        //refresh the virtual file if necessary
    }

    override fun getInputStream(): InputStream {
        // Return an input stream for reading from the virtual file
        throw UnsupportedOperationException()
    }

}

object BuildReportFileSystem : VirtualFileSystem() {

    val INSTANCE = this
    override fun getProtocol(): String {
        return "build-report"
    }

    override fun findFileByPath(path: String): VirtualFile? {
        // Return the virtual file with the given path, if it exists
        return null
    }

    override fun refresh(asynchronous: Boolean) {
        TODO("Not yet implemented")
    }

    override fun refreshAndFindFileByPath(path: String): VirtualFile? {
        // Refresh the virtual file system and return the virtual file with the given path, if it exists
        return null
    }

    override fun addVirtualFileListener(listener: VirtualFileListener) {
        TODO("Not yet implemented")
    }

    override fun removeVirtualFileListener(listener: VirtualFileListener) {
        TODO("Not yet implemented")
    }

    override fun deleteFile(requestor: Any?, vFile: VirtualFile) {
        TODO("Not yet implemented")
    }

    override fun moveFile(requestor: Any?, vFile: VirtualFile, newParent: VirtualFile) {
        TODO("Not yet implemented")
    }

    override fun renameFile(requestor: Any?, vFile: VirtualFile, newName: String) {
        TODO("Not yet implemented")
    }

    override fun createChildFile(requestor: Any?, vDir: VirtualFile, fileName: String): VirtualFile {
        return LightVirtualFile("buildCopy.txt",PlainTextLanguage.INSTANCE,"cant copy")
    }

    override fun createChildDirectory(requestor: Any?, vDir: VirtualFile, dirName: String): VirtualFile {
        return LightVirtualFile("buildCopy.txt",PlainTextLanguage.INSTANCE,"cant copy")
    }

    override fun copyFile(
        requestor: Any?,
        virtualFile: VirtualFile,
        newParent: VirtualFile,
        copyName: String
    ): VirtualFile {
        return LightVirtualFile("buildCopy.txt",PlainTextLanguage.INSTANCE,"cant copy")
    }

    override fun isReadOnly(): Boolean {
        return true
    }

}