package data

import com.squareup.moshi.JsonClass

/**
 * The answer the server gives when it will not serve the version of the plugin that called it.
 *
 * @property error identifies the answer, and is the same string whatever the server's language is
 * @property message is a sentence written by the server, for the clients that can only print what they are
 *           given. The plugin writes its own, from [minimumVersion], and only falls back to this one
 * @property minimumVersion is the oldest version of the plugin that the server serves
 */
@JsonClass(generateAdapter = true)
data class PluginVersionError(
    val error: String? = null,
    val message: String? = null,
    val minimumVersion: String? = null
)
