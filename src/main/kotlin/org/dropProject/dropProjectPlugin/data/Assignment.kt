package data

import com.squareup.moshi.JsonClass

enum class AssignmentInstructionsFormat {
    HTML
}

@JsonClass(generateAdapter = true)
data class Assignment(
    val id: String,
    val name: String,
    val packageName: String,
    val dueDate: String?,
    val submissionMethod: String,
    val language: String,
    val active: Boolean,
    val instructions: Instructions?,
)

@JsonClass(generateAdapter = true)
data class Instructions(
    var format: AssignmentInstructionsFormat? = null,
    var body: String? = null
)

@JsonClass(generateAdapter = true)
data class AssignmentInfoResponse(
    val assignment: Assignment?,
    val errorCode: Int?
)