/*-
 * Plugin Drop Project
 * 
 * Copyright (C) 2019 Yash Jahit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package assignmentTable

import com.intellij.ui.components.JBScrollPane
import com.tfc.ulht.dropProjectPlugin.submissionTable.buildreportUI
import data.FullBuildReport
import java.awt.Dimension
import javax.swing.JFrame


class FullBuildReportTableColumn(fullBuildReport: FullBuildReport) : JFrame(){

    private val frame = JFrame("Submission Report")

    init {

        /*table.columnModel.getColumn(selectSubmission).cellRenderer =
            AssignmentTableButtonRenderer("Show Submission Report")
        table.columnModel.getColumn(selectSubmission).cellEditor = SubmissionTableButtonEditor(JTextField(), "Show Submission Report", frame)*/

        val scrollPane = JBScrollPane(buildreportUI(fullBuildReport))


        //frame composition
        frame.isLocationByPlatform = true
        frame.isResizable=false
        frame.contentPane = scrollPane
        frame.contentPane.preferredSize = Dimension(525, 700)
        //frame.maximumSize = Dimension(530,Int.MAX_VALUE)
        //frame.minimumSize = Dimension(525,10)


        frame.pack()
        frame.isVisible = true
    }
}