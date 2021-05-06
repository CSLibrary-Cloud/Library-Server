package com.cslibrary.library.service

import com.cslibrary.library.data.User
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
}