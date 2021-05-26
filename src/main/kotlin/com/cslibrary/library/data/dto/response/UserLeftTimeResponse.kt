package com.cslibrary.library.data.dto.response

data class UserLeftTimeResponse(
    var reservedSeat: SeatSelectResponse,
    var leftTime: Long
)