package data

import com.squareup.moshi.JsonClass

enum class AssignmentInstructionsFormat {
    HTML
}

/**
 * The shape the server expects a submission to this assignment to have.
 *
 * COMPACT is the structure Drop Project has always used: sources under src/<package>, and the server builds
 * the maven project around them. MAVEN means the student writes the maven project themselves, so the
 * submission has to carry its pom.xml and put the sources where maven expects them.
 */
enum class SubmissionStructure {
    COMPACT,
    MAVEN;

    companion object {
        /**
         * Reads what the server reported, falling back to [COMPACT] for a server too old to report anything
         * and for a structure this version of the plugin does not know about: submitting the way the plugin
         * always has is the answer that is right in the first case and no worse than refusing in the second.
         */
        fun of(reported: String?) = entries.find { it.name.equals(reported?.trim(), ignoreCase = true) } ?: COMPACT
    }
}

@JsonClass(generateAdapter = true)
data class Assignment(
    val id: String,
    val name: String,
    val packageName: String,
    val dueDate: String?,
    val submissionMethod: String,
    val language: String,
    val submissionStructure: String? = null,
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