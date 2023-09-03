package org.dropProject.dropProjectPlugin.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class SettingsComponent {
    private val mainPanel: JPanel
    private val serverURL = JBTextField()
    private var nameField = JBTextField()
    private val numberField = JBTextField()
    private val tokenField = JBPasswordField()
    private val showToken = JBCheckBox("Show")
    private val tokenpanel = JPanel(BorderLayout())

    init {
        //TOKEN FIELD AND SHOW CHECKBOX COMBINED
        tokenpanel.add(tokenField, BorderLayout.CENTER)
        tokenpanel.add(showToken, BorderLayout.EAST)

        //BUILD SETTINGS FORM
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Server URL: "), serverURL, 1, false)
            .addLabeledComponent(JBLabel("Name: "), nameField, 1, false)
            .addLabeledComponent(JBLabel("Number: "), numberField, 1, false)
            .addLabeledComponent(JBLabel("Token: "), tokenpanel, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        showToken.addActionListener {
            val checkbox = it.source as JCheckBox
            tokenField.echoChar = if (checkbox.isSelected) 0.toChar() else '\u2022'
        }
    }

    fun getPanel(): JPanel {
        return mainPanel
    }

    fun getServerURL(): String {
        return serverURL.text
    }

    fun setServerURL(@NotNull text: String) {
        serverURL.text = text
    }

    fun getNameField(): String {
        return nameField.text
    }

    fun setNameField(@NotNull text: String) {
        nameField.text = text
    }

    fun getNumberField(): String {
        return numberField.text
    }

    fun setNumberField(@NotNull text: String) {
        numberField.text = text
    }

    fun getTokenField(): String {
        return String(tokenField.password)
    }

    fun setTokenField(token: String) {
        tokenField.text = token
    }

    fun getPreferredFocusedComponent(): JComponent {
        return nameField
    }

}