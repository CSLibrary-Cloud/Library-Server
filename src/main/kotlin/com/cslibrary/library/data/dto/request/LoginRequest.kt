package com.cslibrary.library.data.dto.request

data class LoginRequest(
    var userId: String = "",
    var userPassword: String = ""
)