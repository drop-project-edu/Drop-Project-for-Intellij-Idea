package com.tfc.ulht.dropProjectPlugin.assignmentComponents

import com.tfc.ulht.dropProjectPlugin.Globals
import com.tfc.ulht.dropProjectPlugin.loginComponents.Authentication
import okhttp3.Request

class AssignmentInstructions(assignmentID: String){


    private val REQUEST_URL = "${Globals.REQUEST_URL}/api/student/assignment"
    private var htmlFragment : String = ""
    /*private var CSS frag (idea)*/

    init {
        val request = Request.Builder()
            .url("$REQUEST_URL/$assignmentID/instructions")
            .build()

        Authentication.httpClient.newCall(request).execute().use { response ->

            htmlFragment = response.body!!.string()

        }

    }

     fun getInstructionsFragment(): String {

        return htmlFragment
    }
}