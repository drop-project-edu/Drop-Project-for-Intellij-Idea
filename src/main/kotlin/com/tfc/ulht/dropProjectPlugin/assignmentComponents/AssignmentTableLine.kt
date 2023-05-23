package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.ui.components.JBRadioButton
import data.Instructions


class AssignmentTableLine {

    lateinit var name: String
    lateinit var language: String
    lateinit var dueDate: String
    lateinit var id_notVisible: String
    lateinit var radioButton: JBRadioButton
    var instructions: Instructions? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AssignmentTableLine

        return id_notVisible == other.id_notVisible
    }

    override fun hashCode(): Int {
        return id_notVisible.hashCode()
    }

}