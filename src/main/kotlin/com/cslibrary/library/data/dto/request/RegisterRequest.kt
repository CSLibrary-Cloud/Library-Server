package com.cslibrary.library.data.dto.request

import com.cslibrary.library.data.User

data class RegisterRequest(
    var userId: String = "",
    var userPassword: String = "",
    var userName: String = "",
    var userPhoneNumber: String = ""
) {
    fun toUser(): User = User(
        userId = this.userId,
        userPassword = this.userPassword,
        userName = this.userName,
        userPhoneNumber = this.userPhoneNumber,
        roles = setOf("ROLE_USER")
    )
}
