package com.cslibrary.library.data.dto.response

import com.cslibrary.library.data.UserState

// User information for Admin
data class SealedUser(
    var userId: String,
    var userName: String,
    var userPhoneNumber: String,
    var leftTime: Long,
    var totalStudyTime: Long,
    var reservedSeatNumber: String,
    var userState: UserState,
    var userNonBanned: Boolean
)