package com.cslibrary.library.data.dto.response

import com.cslibrary.library.data.UserNotification
import com.cslibrary.library.data.dto.LeaderBoard

data class SaveLeftTimeResponse(
    var leaderBoardList: List<LeaderBoard>, // Leaderboard as List
    var userNotificationList: List<UserNotification>
)
