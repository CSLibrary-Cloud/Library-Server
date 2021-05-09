package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.dto.response.SeatResponse
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SeatService {
    private var userSeatInfo: HashMap<Int, User?> = HashMap()

    @PostConstruct
    fun initSeats() {
        for (i in 0..29) {
            userSeatInfo[i] = null
        }
    }

    fun getAllSeats(): List<SeatResponse> = userSeatInfo.map {
        SeatResponse(
            seatNumber = it.key,
            isUsing = checkValue(it.value)
        )
    }.toList()

    private fun checkValue(user: User?): Boolean = (user != null)
}