package com.cslibrary.library.data.dto.request

import com.cslibrary.library.data.UserNotification

data class NotifyUserRequest(
    var userId: String,
    var userNotification: UserNotification
)
