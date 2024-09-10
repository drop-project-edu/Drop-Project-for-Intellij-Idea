package org.dropProject.dropProjectPlugin.loginComponents

import com.intellij.CommonBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.*
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import com.intellij.util.ui.JBUI
import org.dropProject.dropProjectPlugin.DefaultNotification
import org.dropProject.dropProjectPlugin.User
import org.dropProject.dropProjectPlugin.settings.SettingsState
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.awt.Desktop
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*

class Login(private val toolWindow: DropProjectToolWindow) {

    private lateinit var mainpanel: JPanel
    private lateinit var serverField: JComboBox<Server>
    private lateinit var nameField: JTextField
    private lateinit var useridField: JTextField
    private lateinit var tokenField: JPasswordField
    private lateinit var showCheckBox: JCheckBox
    private lateinit var addStudentButton: JButton
    private lateinit var addNameField: JTextField
    private lateinit var addNumberField: JTextField
    private lateinit var tokenPanel: JPanel
    private lateinit var numberPanel: JPanel
    private lateinit var serverPanel: JPanel
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

    // for dropdown of servers
    data class Server(val serverName: String, val serverUrl: String) {
        override fun toString(): String {
            return serverName
        }
    }

    val servers = arrayOf(
        Server("", ""), // Empty option
        Server("Lusófona University", "https://deisi.ulusofona.pt/drop-project"),
        Server("Drop Project Playground", "https://playground.dropproject.org/dp")
    )

    private fun validateRequiredFormFields(vararg fields: JTextField): Boolean {
        val requiredFields = fields.asList()
        val project = toolWindow.project

        for (field in requiredFields) {
            if (field.text.trim().isEmpty()) {
                Messages.showMessageDialog(project, "${field.accessibleContext.accessibleName} is required.", CommonBundle.getErrorTitle(), Messages.getErrorIcon())
                field.requestFocus()
                return false
            }
        }
        return true
    }

    fun show() {

        val project = toolWindow.project

        val dialogBuilder = DialogBuilder().title("Drop Project Log In")
        dialogBuilder.setCenterPanel(assemble())
        val okActionDescriptor = DialogBuilder.OkActionDescriptor()
        okActionDescriptor.setText("Log In")
        dialogBuilder.setActionDescriptors(okActionDescriptor, DialogBuilder.CancelActionDescriptor())
        dialogBuilder.setOkOperation {
            if (validateRequiredFormFields(nameField, useridField, tokenField)) {
                dialogBuilder.dialogWrapper.close(DialogWrapper.OK_EXIT_CODE);
            }
        }
        val option = dialogBuilder.show()

//        val opt = arrayOf("Log In", "Cancel")
//        //UIManager.put("OptionPane.minimumSize", Dimension(panel.width, panel.height))
//        val option = JOptionPane.showOptionDialog(
//            null, assemble(), "Drop Project Log In",
//            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION, null, opt, opt[0]
//        )

        if (option == 0) {

            // check server
            val selectedServer = serverField.selectedItem as Server
            if (selectedServer.serverName == "") {
                Messages.showMessageDialog(project, "Please select a server", CommonBundle.getErrorTitle(), Messages.getErrorIcon())
                return
            }

            //add student to list
            studentNameField.add(nameField)
            studentNumberField.add(useridField)
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

            if (empty) {
                Messages.showMessageDialog(project, "Student info is empty", CommonBundle.getErrorTitle(), Messages.getErrorIcon())
            }
            else {
                authenticate(project)
            }
        }
    }

    private fun manageSettingsChanges() {
        val settingsState = SettingsState.getInstance()
        if (!(nameField.text.equals(settingsState.username) && useridField.text.equals(settingsState.usernumber) &&
                    settingsState.token == String(tokenField.password))
        ) {

            val updateSettingsDialog = MessageDialogBuilder.yesNo("Drop Project Settings Update", "Do you want to update log-in settings for faster login?")
            val result = updateSettingsDialog.ask(toolWindow.project)

//            val updateSettings = JOptionPane.showOptionDialog(
//                null, "Do you want to update log-in settings for faster login?", "Drop Project Settings Update",
//                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION, null, arrayOf("Yes", "No"), null
//            )
            if (result) {
                settingsState.updateValues((serverField.selectedItem as Server).serverUrl, nameField.text, useridField.text, String(tokenField.password))
            }
        }
    }

    private fun fillFieldsFromSettings() {
        val settingsState = SettingsState.getInstance()
        serverField.selectedItem = servers.find { it.serverUrl == settingsState.serverURL }
        nameField.text = settingsState.username
        useridField.text = settingsState.usernumber
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
        mainpanel.layout = GridLayoutManager(9, 2, JBUI.emptyInsets(), -1, -1)
        val labelServer = JLabel("Server")
        mainpanel.add(
            labelServer,
            GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false
            )
        )
        val label1 = JLabel()
        label1.text = "Name"
        mainpanel.add(
            label1,
            GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false
            )
        )
        val label2 = JLabel()
        label2.text = "User Id"
        mainpanel.add(
            label2,
            GridConstraints(2, 0, 1, 1,
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
            GridConstraints(3, 0, 1, 1,
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
        tokenField = JPasswordField()
        tokenField.accessibleContext.accessibleName = "Token"
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
        useridField = JTextField()
        useridField.accessibleContext.accessibleName = "User Id"
        numberPanel.add(
            useridField,
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

        serverPanel = JPanel()
        serverPanel.layout = GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            serverPanel,
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

        serverField = ComboBox(servers)
        serverField.addActionListener {
            val settings: SettingsState = SettingsState.getInstance()
            settings.serverURL = (serverField.selectedItem as? Server)?.serverUrl ?: ""
        }
        serverPanel.add(
            serverField,
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
        nameField = JTextField()
        nameField.accessibleContext.accessibleName = "Name"
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
        val panel1 = JPanel()
        panel1.layout = GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1)
        mainpanel.add(
            panel1,
            GridConstraints(
                8,
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
                6,
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
                6,
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
                if (toolWindow.globals.REQUEST_URL != "") {
                    Desktop.getDesktop().browse(URI("${toolWindow.globals.REQUEST_URL}/personalToken"))
                }
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