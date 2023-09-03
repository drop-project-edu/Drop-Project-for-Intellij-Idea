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
    val overdue: Boolean? = null

)

@JsonClass(generateAdapter = true)
data class Summary(
    val reportGoal: Int?,
    val reportKey: String,
    val reportProgress: Int?,
    val reportValue: String
)