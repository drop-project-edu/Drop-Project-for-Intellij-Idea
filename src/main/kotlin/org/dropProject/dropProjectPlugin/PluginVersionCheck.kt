package org.dropProject.dropProjectPlugin

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.squareup.moshi.Moshi
import data.PluginVersionError
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import java.util.Properties

/**
 * The plugin's half of the version check that the server runs on every call to its student API.
 *
 * The plugin says which version it is in the User-Agent of every request, and the server refuses the ones it
 * no longer supports. That refusal is the only thing the student would otherwise see of it: a login that
 * does not work, or a submission that is never validated, with nothing saying that the plugin is what needs
 * fixing. So the refusal is recognized wherever the plugin talks to the server, and turned into a message
 * that says what to do about it.
 */
object PluginVersionCheck {

    /** the error that the server names its refusal with, whatever language it writes its message in */
    private const val SERVER_ERROR = "Plugin version not supported"

    private const val HTTP_UPGRADE_REQUIRED = 426

    /** more than enough for the refusal, and never enough to hold a build report in memory */
    private const val PEEKED_BYTES = 4096L

    private const val TITLE = "Outdated Drop Project Plugin"

    /** written by the build, from the pluginVersion in gradle.properties */
    private const val VERSION_RESOURCE = "/drop-project-plugin.properties"

    private val logger = Logger.getInstance(PluginVersionCheck::class.java)

    private val errorJsonAdapter = Moshi.Builder().build().adapter(PluginVersionError::class.java)

    /**
     * The version of this plugin, as it is published to the marketplace.
     *
     * It is read from a resource that the build writes from the same pluginVersion that it publishes under
     * (see generateVersionResource in build.gradle.kts), and not from the plugin descriptor: the descriptor
     * is reached through platform APIs that have moved between the IDE builds this plugin supports, and
     * whose drift is only found by running the plugin verifier.
     */
    val version: String by lazy {
        val properties = Properties()
        val resource = PluginVersionCheck::class.java.getResourceAsStream(VERSION_RESOURCE)
        if (resource == null) {
            logger.warn("$VERSION_RESOURCE is missing, so the server cannot be told which version this is")
        } else {
            resource.use { properties.load(it) }
        }
        properties.getProperty("version") ?: "unknown"
    }

    /**
     * What every request tells the server about itself, e.g.
     * "DropProjectPlugin/0.9.15 (IntelliJ IDEA 2024.3.1)". The server reads the version out of it to decide
     * whether it still serves this plugin; the rest is for whoever reads the server's logs.
     */
    val userAgent: String by lazy {
        "DropProjectPlugin/$version (${ApplicationNamesInfo.getInstance().fullProductName} " +
                "${ApplicationInfo.getInstance().fullVersion})"
    }

    /**
     * Puts [userAgent] on every request, in place of the one okhttp writes when nobody sets it. Up to 0.9.14
     * nobody did, which is how a server tells this plugin from the versions that came before it.
     */
    val userAgentInterceptor = Interceptor { chain ->
        chain.proceed(chain.request().newBuilder().header("User-Agent", userAgent).build())
    }

    /**
     * Tells the student that this plugin is too old for the server they are calling, when that is what
     * [response] is, and answers whether it was. A caller that gets true stops what it was doing with the
     * response: it carries nothing else.
     *
     * The message is written here rather than taken from the server so that it can name the version the
     * student is running, which the server does not always know, and say it in the language of the IDE.
     */
    fun reportIfOutdated(response: Response, project: Project?): Boolean {
        val refusal = refusal(response) ?: return false

        logger.warn("The server refused to serve this plugin (version $version): " +
                "it requires ${refusal.minimumVersion ?: "a newer version"}")

        val explanation = if (refusal.minimumVersion != null) {
            "Your Drop Project plugin (version $version) is too old for this server, " +
                    "which requires version ${refusal.minimumVersion} or later."
        } else {
            // a server that does not say which version it wants leaves nothing but what it wrote
            refusal.message ?: "Your Drop Project plugin is too old for this server."
        }

        showError(project, "$explanation\n\nPlease update it in Settings | Plugins.")
        return true
    }

    /**
     * Reads [response] as the server's refusal, or answers null when it is any other response.
     *
     * The body is peeked and not read, so that a response that turns out to be something else is left intact
     * for the caller that was going to read it.
     */
    private fun refusal(response: Response): PluginVersionError? {
        // the plugins that report a version are refused with a 426. The 401 is what the ones that cannot are
        // refused with, and is answered here too, in case this plugin ever fails to read its own version
        if (response.code != HTTP_UPGRADE_REQUIRED && response.code != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return null
        }
        val refusal = try {
            errorJsonAdapter.fromJson(response.peekBody(PEEKED_BYTES).string())
        } catch (e: Exception) {
            logger.debug("Could not read the body of a ${response.code} as a plugin version error", e)
            null
        }
        return refusal?.takeIf { it.error == SERVER_ERROR }
    }

    private fun showError(project: Project?, message: String) {
        val application = ApplicationManager.getApplication()
        if (application.isDispatchThread) {
            Messages.showMessageDialog(project, message, TITLE, Messages.getErrorIcon())
        } else {
            application.invokeLater { Messages.showMessageDialog(project, message, TITLE, Messages.getErrorIcon()) }
        }
    }
}
