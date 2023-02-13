package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.ui.components.JBRadioButton
import data.Instructions


class TableLine {

    lateinit var name : String
    lateinit var language : String
    lateinit var dueDate : String
    lateinit var id_notVisible : String
    lateinit var radioButton : JBRadioButton
    var instructions: Instructions? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableLine

        if (id_notVisible != other.id_notVisible) return false

        return true
    }

    override fun hashCode(): Int {
        return id_notVisible.hashCode()
    }

}