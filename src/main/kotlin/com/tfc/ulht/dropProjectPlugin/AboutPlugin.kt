package com.tfc.ulht.dropProjectPlugin

/**
 *  Open authors LinkedIn webpage
 */

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import java.net.URI
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JOptionPane


class AboutPlugin : DumbAware, AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val text = "This plugin is designed to help students submit their project\n" +
                "on the Drop Project platform without needing to create a zip.\n\n\n" +
                "Plugin made by Yash Jahit" +
                "Improved by Bernardo Baltazar"

        JOptionPane.showMessageDialog(
            null,
            text,
            "About",
            JOptionPane.INFORMATION_MESSAGE,
            imageIconCreate()
        )

    }

    fun openURI() {
        val githubLink = "https://github.com/bernardovlbaltazar/Plugin-Drop-Project"
        val url = URI(githubLink)
        try {
            val desktop = java.awt.Desktop.getDesktop()
            desktop.browse(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun imageIconCreate(): Icon {

        val pluginLogo = ImageIcon(this.javaClass.classLoader.getResource("images/plugin_dp_logo_smaller.png")).image
        val icon = ImageIcon(pluginLogo)

        return icon
    }

}


