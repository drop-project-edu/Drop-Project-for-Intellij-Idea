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

package data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FullBuildReport(
    val assignment: Assignment?,
    val buildReport: BuildReport?,
    val numSubmissions: Int?,
    var error: String? = null,
    val structureErrors: List<String>?,
    val submission: Submission?,
    val summary: List<Summary>?
)

@JsonClass(generateAdapter = true)
data class BuildReport(
    val checkstyleErrors: List<String>? = null,
    val compilationErrors: List<String>? = null,
    val junitErrorsStudent: String? = null,
    val junitErrorsTeacher: String? = null,
    val junitSummaryStudent: String? = null,
    val junitSummaryTeacher: String? = null,
    val junitSummaryTeacherExtraDescription: String? = null


)
@JsonClass(generateAdapter = true)
data class Submission(
    val status: String,
    val statusDate: String,
    val submissionDate: String,
    val overdue : Boolean? = null

)

@JsonClass(generateAdapter = true)
data class Summary(
    val reportGoal: Int?,
    val reportKey: String,
    val reportProgress: Int?,
    val reportValue: String
)