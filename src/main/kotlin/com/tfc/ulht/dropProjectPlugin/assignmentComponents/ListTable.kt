package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import com.tfc.ulht.dropProjectPlugin.Globals
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JOptionPane
import javax.swing.JTable
import javax.swing.ListSelectionModel


class ListTable(model: ListTableModel<TableLine>?) : TableView<TableLine>(model) {


    init {
        getSelectionModel().selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellSelectionEnabled = true
        isStriped = true
        cursor = Cursor(Cursor.HAND_CURSOR)
        autoCreateRowSorter = true
        emptyText.text = "Login to see your Assignments"
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val source = e.source as JTable
                val rowAtPoint = source.rowAtPoint(e.point)
                if (MouseEvent.BUTTON1==e.button && e.clickCount==2 && !e.isConsumed && rowAtPoint!=-1)  {
                    val nameOfSelected = this@ListTable.getValueAt(rowAtPoint,0).toString()
                    if (Globals.selectedAssignmentID!=nameOfSelected) {

                        Globals.selectedAssignmentID = nameOfSelected
                        JOptionPane.showMessageDialog(null,"<html>The Assignment <b>$nameOfSelected</b> Was Selected</html>")
                    }

                }
                val columnAtPoint = source.columnAtPoint(e.point)
                // Link column = 2
                if ( columnAtPoint == 3  && MouseEvent.BUTTON1 == e.button) {
                    // TODO: assignment details
                }
                super.mousePressed(e)
            }
        })
    }

}