package org.dropProject.dropProjectPlugin.loginComponents

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe


class CredentialsController {

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        return CredentialAttributes(
            generateServiceName("My System", key)
        )
    }

    fun storeCredentials(username: String, password: String, serverId: String) {

        val credentialAttributes = createCredentialAttributes(serverId)
        val credentials = Credentials(username, password)
        PasswordSafe.instance.set(credentialAttributes, credentials)
    }

    fun removeStoredCredentials(serverId: String) {
        val credentialAttributes = createCredentialAttributes(serverId)
        PasswordSafe.instance.set(credentialAttributes, null)
    }

    fun retrieveStoredCredentials(serverId: String): Credentials? {

        val credentialAttributes = createCredentialAttributes(serverId)

        return PasswordSafe.instance.get(credentialAttributes)
    }
}

