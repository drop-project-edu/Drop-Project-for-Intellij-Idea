package com.tfc.ulht.dropProjectPlugin

class User(studentNumber: String, studentName: String) {

    val number = studentNumber
    val name = studentName

    override fun toString(): String {
        return "$number;$name"
    }


}