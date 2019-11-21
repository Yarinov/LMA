package com.yarinov.lma.User

class User {

    var userName: String? = null
    var userId: String? = null

    constructor(name: String?, image: String?) {
        this.userName = name
        this.userId = image
    }

    fun setNames(name: String) {
        this.userName = name
    }

    fun getNames(): String {
        return userName.toString()
    }

    fun getId(): String {
        return userId.toString()
    }

    fun setId(name: String) {
        this.userId = userId
    }

    override fun toString(): String {
        return "User(userName=$userName, userId=$userId)"
    }


}