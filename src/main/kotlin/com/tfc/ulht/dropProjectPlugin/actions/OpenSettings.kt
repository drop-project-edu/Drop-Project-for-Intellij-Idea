package com.tfc.ulht.dropProjectPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project

class OpenSettings(private val project: Project) :
    DumbAwareAction("Open Settings", "Drop project settings", AllIcons.General.Settings) {
    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Drop Project Settings")
    }
}