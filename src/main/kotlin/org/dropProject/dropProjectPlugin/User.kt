package org.dropProject.dropProjectPlugin

class User(studentNumber: String, studentName: String) {

    private val number = studentNumber
    val name = studentName

    override fun toString(): String {
        return "$number;$name"
    }


}