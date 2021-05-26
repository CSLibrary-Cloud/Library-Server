package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.dto.response.SeatResponse
import com.cslibrary.library.error.exception.ConflictException
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

    fun reserveSeat(user: User, reserveSeatNumber: Int): Int {
        checkSeatOrElse(reserveSeatNumber) {
            userSeatInfo[reserveSeatNumber] = user
        }
        return reserveSeatNumber
    }

    fun changeSeat(user: User, newReserveSeatNumber: Int): Int {
        // Null-fy current seat
        userSeatInfo[user.reservedSeatNumber.toInt()] = null

        // Reserve Seat
        return reserveSeat(user, newReserveSeatNumber)
    }

    fun removeSeat(user: User) {
        // Null-fy current seat
        userSeatInfo[user.reservedSeatNumber.toInt()] = null
    }

    private fun checkSeatOrElse(userSeatNumber: Int, onSuccess: () -> Unit) {
        if (userSeatInfo[userSeatNumber] != null) {
            // Error
            throw ConflictException("Username already exists!")
        } else {
            onSuccess()
        }
    }

    private fun checkValue(user: User?): Boolean = (user != null)
}