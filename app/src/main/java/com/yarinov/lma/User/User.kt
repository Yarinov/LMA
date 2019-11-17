package com.yarinov.lma.User

class User {

    var userName: String? = null
    var userImage: String? = null

    constructor(name: String?, image: String?) {
        this.userName = name
        this.userImage = image
    }

    fun setNames(name: String) {
        this.userName = name
    }

    fun getNames(): String {
        return userName.toString()
    }

    fun getImage(): String {
        return userImage.toString()
    }

    fun setImage(name: String) {
        this.userImage = userImage
    }




}