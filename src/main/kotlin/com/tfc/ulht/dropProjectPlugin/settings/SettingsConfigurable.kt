package com.tfc.ulht.dropProjectPlugin.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class SettingsConfigurable : Configurable {
    private var mySettingsComponent: SettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "SDK: Application Settings Example"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return mySettingsComponent?.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent {
        mySettingsComponent = SettingsComponent()
        return mySettingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings: SettingsState = SettingsState.getInstance()
        return (!mySettingsComponent?.getServerURL().equals(settings.serverURL)) or
                (!mySettingsComponent?.getNameField().equals(settings.username)) or
                (!mySettingsComponent?.getNumberField().equals(settings.usernumber)) or
                (!mySettingsComponent?.getTokenField().contentEquals(settings.token))
    }

    override fun apply() {
        val settings: SettingsState = SettingsState.getInstance()
        settings.serverURL = mySettingsComponent?.getServerURL()!!
        settings.username = mySettingsComponent?.getNameField()!!
        settings.usernumber = mySettingsComponent?.getNumberField()!!
        settings.token = mySettingsComponent?.getTokenField()!!
    }

    override fun reset() {
        val settings: SettingsState = SettingsState.getInstance()
        settings.serverURL.let { mySettingsComponent?.setServerURL(it) }
        settings.username.let { mySettingsComponent?.setNameField(it) }
        settings.usernumber.let { mySettingsComponent?.setNumberField(it) }
        settings.token.let { mySettingsComponent?.setTokenField(it) }

    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}