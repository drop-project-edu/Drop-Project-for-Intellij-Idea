package com.tfc.ulht.dropProjectPlugin.loginComponents

import com.intellij.openapi.project.Project
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import com.intellij.util.ui.JBUI
import com.tfc.ulht.dropProjectPlugin.DefaultNotification
import com.tfc.ulht.dropProjectPlugin.User
import com.tfc.ulht.dropProjectPlugin.settings.SettingsState
import com.tfc.ulht.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.awt.Desktop
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*

class Login(private val toolWindow: DropProjectToolWindow) {

    private lateinit var mainpanel: JPanel
    private lateinit var nameField: JTextField
    private lateinit var numberField: JTextField
    private lateinit var tokenField: JPasswordField
    private lateinit var showCheckBox: JCheckBox
    private lateinit var addStudentButton: JButton
    private lateinit var addNameField: JTextField
    private lateinit var addNumberField: JTextField
    private lateinit var tokenPanel: JPanel
    private lateinit var numberPanel: JPanel
    private lateinit var namePanel: JPanel
    private lateinit var tokenLinkPanel: JPanel
    private lateinit var tokenLinkLabel: JLabel
    private lateinit var addStudentPanel: JPanel
    private lateinit var addNumberLabel: JLabel
    private lateinit var addNameLabel: JLabel
    private lateinit var add2ndStudentPanel: JPanel
    private lateinit var add2ndStudentNameLabel: JLabel
    private lateinit var add2ndStudentNameField: JTextField
    private lateinit var add2ndStudentNumberLabel: JLabel
    private lateinit var add2ndStudentNumberField: JTextField

    private val studentNumberField = mutableListOf<JTextField>()
    private val studentNameField = mutableListOf<JTextField>()

    private var addCount = 0

    init {
        val opt = arrayOf("Log In", "Cancel")
        //UIManager.put("OptionPane.minimumSize", Dimension(panel.width, panel.height))
        val option = JOptionPane.showOptionDialog(
            null, assemble(), "Drop Project Log In",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION, null, opt, opt[0]
        )

        if (option == 0) {
            //add student to list
            studentNameField.add(nameField)
            studentNumberField.add(numberField)
            var empty = false
            if (addCount % 3 >= 1) {
                if (addNameField.text.isEmpty() || addNumberField.text.isEmpty()) {
                    empty = true
                } else {
                    studentNameField.add(addNameField)
                    studentNumberField.add(addNumberField)
                }

            }
            if (addCount % 3 >= 2) {
                if (add2ndStudentNameField.text.isEmpty() || add2ndStudentNumberField.text.isEmpty()) {
                    empty = true
                } else {
                    studentNameField.add(add2ndStudentNameField)
                    studentNumberField.add(add2ndStudentNumberField)
                }

            }

            if (empty) JOptionPane.showMessageDialog(
                null, "Student info is empty", "Error",
                JOptionPane.ERROR_MESSAGE
            )
            else {
                authenticate(toolWindow.project)
            }
        }
    }

    private fun manageSettingsChanges() {
        val settingsState = SettingsState.getInstance()
        if (!(nameField.text.equals(settingsState.username) && numberField.text.equals(settingsState.usernumber) &&
                    settingsState.token == String(tokenField.password))
        ) {
            val updateSettings = JOptionPane.showOptionDialog(
                null, "Do you want to update log-in settings for faster login?", "Drop Project Settings Update",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION, null, arrayOf("Yes", "No"), null
            )
            when (updateSettings) {
                0 -> {
                    //YES
                    settingsState
                        .updateValues(nameField.text, numberField.text, String(tokenField.password))
                }
            }
        }
    }

    private fun fillFieldsFromSettings() {
        val settingsState = SettingsState.getInstance()
        nameField.text = settingsState.username
        numberField.text = settingsState.usernumber
        tokenField.text = settingsState.token
    }

    private fun authenticate(project: Project?) {
        val response = toolWindow.authentication
            .loginAuthenticate(
                studentNumberField[0].text.trim(),
                tokenField.password.concatToString()
            )

        UIManager.put("OptionPane.minimumSize", Dimension(200, 100))


        if (response) {
            registerStudents()
            DefaultNotification.notify(project, "Login Successful")
            manageSettingsChanges()

        } else {
            JOptionPane.showMessageDialog(
                null, "Login credentials incorrect!", "Error!",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun registerStudents() {

        toolWindow.studentsList.clear()

        for ((index, number) in studentNumberField.withIndex()) {

            toolWindow.studentsList.add(User(number.text.trim(), studentNameField[index].text.trim()))

        }
    }

    private fun assemble(): JPanel {

        mainpanel = JPanel()
        mainpanel.layout = GridLayoutManager(8, 2, JBUI.emptyInsets(), -1, -1)
        val label1 = JLabel()
        label1.text = "Name"
        mainpanel.add(
            label1,
            GridConstraints(
                0,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        val label2 = JLabel()
        label2.text = "Number"
        mainpanel.add(
            label2,
            GridConstraints(
                1,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        val label3 = JLabel()
        label3.text = "Token"
        mainpanel.add(
            label3,
            GridConstraints(
                2,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        tokenPanel = JPanel()
        tokenPanel.layout = GridLayoutManager(1, 2, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            tokenPanel,
            GridConstraints(
                2,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                null,
                null,
                0,
                false
            )
        )
        tokenField = JPasswordField()
        tokenPanel.add(
            tokenField,
            GridConstraints(
                0,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                Dimension(150, -1),
                null,
                0,
                false
            )
        )
        showCheckBox = JCheckBox()
        showCheckBox.text = "Show"
        tokenPanel.add(
            showCheckBox,
            GridConstraints(
                0,
                1,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        numberPanel = JPanel()
        numberPanel.layout = GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            numberPanel,
            GridConstraints(
                1,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                null,
                null,
                0,
                false
            )
        )
        numberField = JTextField()
        numberPanel.add(
            numberField,
            GridConstraints(
                0,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                Dimension(150, -1),
                null,
                0,
                false
            )
        )
        namePanel = JPanel()
        namePanel.layout = GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            namePanel,
            GridConstraints(
                0,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                null,
                null,
                0,
                false
            )
        )
        nameField = JTextField()
        namePanel.add(
            nameField,
            GridConstraints(
                0,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                Dimension(150, -1),
                null,
                0,
                false
            )
        )
        tokenLinkPanel = JPanel()
        tokenLinkPanel.layout = GridLayoutManager(1, 5, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            tokenLinkPanel,
            GridConstraints(
                3,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                null,
                null,
                0,
                false
            )
        )
        addStudentButton = JButton()
        addStudentButton.text = "Add student"
        tokenLinkPanel.add(
            addStudentButton,
            GridConstraints(
                0,
                0,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        tokenLinkLabel = JLabel()
        tokenLinkLabel.text = "<html><u>Get your token here</u></html>"
        tokenLinkPanel.add(
            tokenLinkLabel,
            GridConstraints(
                0,
                3,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        val spacer1 = Spacer()
        tokenLinkPanel.add(
            spacer1,
            GridConstraints(
                0,
                4,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                1,
                null,
                null,
                null,
                0,
                false
            )
        )
        val spacer2 = Spacer()
        tokenLinkPanel.add(
            spacer2,
            GridConstraints(
                0,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                1,
                null,
                null,
                null,
                0,
                false
            )
        )
        val spacer3 = Spacer()
        tokenLinkPanel.add(
            spacer3,
            GridConstraints(
                0,
                2,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                1,
                null,
                null,
                null,
                0,
                false
            )
        )
        addStudentPanel = JPanel()
        addStudentPanel.layout = GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            addStudentPanel,
            GridConstraints(
                4,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                null,
                null,
                0,
                false
            )
        )
        addNameField = JTextField()
        addStudentPanel.add(
            addNameField,
            GridConstraints(
                0,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                Dimension(150, -1),
                null,
                0,
                false
            )
        )
        addNumberLabel = JLabel()
        addNumberLabel.text = "Number"
        addStudentPanel.add(
            addNumberLabel,
            GridConstraints(
                0,
                1,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        addNumberField = JTextField()
        addStudentPanel.add(
            addNumberField,
            GridConstraints(
                0,
                2,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                Dimension(150, -1),
                null,
                0,
                false
            )
        )
        addNameLabel = JLabel()
        addNameLabel.text = "Name"
        mainpanel.add(
            addNameLabel,
            GridConstraints(
                4,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        val panel1 = JPanel()
        panel1.layout = GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            panel1,
            GridConstraints(
                7,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                null,
                null,
                0,
                false
            )
        )

        add2ndStudentNameLabel = JLabel()
        add2ndStudentNameLabel.text = "Name"
        mainpanel.add(
            add2ndStudentNameLabel,
            GridConstraints(
                5,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        add2ndStudentPanel = JPanel()
        add2ndStudentPanel.layout = GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            add2ndStudentPanel,
            GridConstraints(
                5,
                1,
                1,
                1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                null,
                null,
                0,
                false
            )
        )
        add2ndStudentNameField = JTextField()
        add2ndStudentPanel.add(
            add2ndStudentNameField,
            GridConstraints(
                0,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                Dimension(150, -1),
                null,
                0,
                false
            )
        )
        add2ndStudentNumberLabel = JLabel()
        add2ndStudentNumberLabel.text = "Number"
        add2ndStudentPanel.add(
            add2ndStudentNumberLabel,
            GridConstraints(
                0,
                1,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                0,
                false
            )
        )
        add2ndStudentNumberField = JTextField()
        add2ndStudentPanel.add(
            add2ndStudentNumberField,
            GridConstraints(
                0,
                2,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                Dimension(150, -1),
                null,
                0,
                false
            )
        )
        mainpanel.preferredSize = Dimension(600, 230)

        addStudentPanel.isVisible = false
        addNameLabel.isVisible = false
        add2ndStudentPanel.isVisible = false
        add2ndStudentNameLabel.isVisible = false
        actionListeners()
        fillFieldsFromSettings()
        return mainpanel
    }

    private fun actionListeners() {
        tokenLinkLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                Desktop.getDesktop().browse(URI("${toolWindow.globals.REQUEST_URL}/personalToken"))
            }
        })
        addStudentButton.addActionListener {
            when (++addCount % 3) {
                0 -> {
                    //CLEAR TEXT FIELDS
                    addNameField.text = ""
                    addNumberField.text = ""
                    add2ndStudentNameField.text = ""
                    add2ndStudentNumberField.text = ""
                    //make invisible
                    addNameLabel.isVisible = false
                    add2ndStudentNameLabel.isVisible = false
                    addStudentPanel.isVisible = false
                    add2ndStudentPanel.isVisible = false
                }

                1 -> {
                    addNameLabel.isVisible = true
                    addStudentPanel.isVisible = true
                }

                2 -> {
                    add2ndStudentNameLabel.isVisible = true
                    add2ndStudentPanel.isVisible = true
                }
            }
        }
        showCheckBox.addActionListener {
            val checkbox = it.source as JCheckBox
            tokenField.echoChar = if (checkbox.isSelected) 0.toChar() else '\u2022'

        }
    }
}