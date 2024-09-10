package org.dropProject.dropProjectPlugin.loginComponents

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsContexts
import com.jetbrains.rd.util.use
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.*
import org.apache.http.HttpStatus
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.io.IOException
import java.net.ConnectException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class Authentication(private val toolWindow: DropProjectToolWindow) {

    var httpClient = OkHttpClient()
    var alreadyLoggedIn = false
        set(value) {
            field = value
            if (value) {
                toolWindow.toolbarPanel!!.loggedInToolbar()
            } else {
                toolWindow.toolbarPanel!!.loggedOutToolbar()
            }
        }


    private val REQUEST_URL: String
        get() {
            return "${toolWindow.globals.REQUEST_URL}/api/student/assignments/current"
        }

    fun loginAuthenticate(username: String, token: String): Boolean {
        alreadyLoggedIn = request(username, token)
        return alreadyLoggedIn
    }

    /*fun onStartAuthenticate(username: String, token: String): Boolean {
        alreadyLoggedIn = request(username, token)
        return alreadyLoggedIn
    }*/

    private fun request(username: String, token: String): Boolean {
        httpClient = OkHttpClient.Builder()
            .authenticator(object : Authenticator {
                @Throws(IOException::class)
                override fun authenticate(route: Route?, response: Response): Request? {
                    if (response.request.header("Authorization") != null) {
                        return null // Give up, we've already attempted to authenticate.
                    }

                    return response.request.newBuilder()
                        .header("Authorization", Credentials.basic(username, token))
                        .build()
                }
            })
            .ignoreAllSSLErrors()
            .build()
        val request = Request.Builder()
            .url(REQUEST_URL)
            .build()
        try {
            httpClient.newCall(request).execute().use { response ->
                if (response.code == HttpStatus.SC_UNAUTHORIZED) {
                    val jsonStr = response.body?.string()
                    val moshi = Moshi.Builder().build()
                    val type = Types.newParameterizedType(MutableMap::class.java, String::class.java, Any::class.java)
                    val jsonAdapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)
                    val result: Map<String, String>? = jsonAdapter.fromJson(jsonStr)
                    if (result != null) {
                        Messages.showMessageDialog(
                            result["message"],
                            "Authentication failed",
                            Messages.getErrorIcon()
                        )
                    }
                }
                return response.isSuccessful
            }
        } catch (e: ConnectException) {
            Messages.showMessageDialog(
                "Drop project server connection refused",
                "DP Server Off", Messages.getErrorIcon()
            )
            return false
        }

    }


    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier { _, _ -> true }
        return this
    }
}