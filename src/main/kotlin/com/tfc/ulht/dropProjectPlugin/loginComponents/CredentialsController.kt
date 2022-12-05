package com.tfc.ulht.dropProjectPlugin.loginComponents

import java.io.File

class CredentialsController {

    companion object {
        lateinit var e: String
    }

    fun encryptPassword(username: String, password: String) {
        val enc = String(encrypt(password.toByteArray()))

        val file = File("$e\\up.txt")
        file.writeText("$username;$enc")

    }


    private fun encrypt(textToEncrypt: ByteArray): ByteArray {
        val enc = ByteArray(textToEncrypt.size)

        for (i in textToEncrypt.indices) {
            enc[i] = (if ((i % 2 == 0)) textToEncrypt[i] + 1 else textToEncrypt[i] - 1).toByte()
        }

        return enc
    }

    fun decrypt(textToDecrypt: ByteArray): ByteArray {
        val enc = ByteArray(textToDecrypt.size)

        for (i in textToDecrypt.indices) {
            enc[i] = (if ((i % 2 == 0)) textToDecrypt[i] - 1 else textToDecrypt[i] + 1).toByte()
        }

        return enc
    }


}