package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.UserState
import com.cslibrary.library.data.dto.request.*
import com.cslibrary.library.data.dto.response.LoginResponse
import com.cslibrary.library.data.dto.response.RegisterResponse
import com.cslibrary.library.data.dto.response.SeatSelectResponse
import com.cslibrary.library.data.dto.response.UserLeftTimeResponse
import com.cslibrary.library.error.exception.ConflictException
import com.cslibrary.library.error.exception.ForbiddenException
import com.cslibrary.library.error.exception.NotFoundException
import com.cslibrary.library.security.JWTTokenProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JWTTokenProvider,
    private val seatService: SeatService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun findUserByToken(userToken: String): User {
        val userName: String = jwtTokenProvider.getUserPk(userToken)
        return runCatching {
            userRepository.findByUserId(userName)
        }.getOrElse {
            logger.error("User is not found! UserName on token: $userName")
            throw it
        }
    }

    private fun isUserAccountExists(userId: String) {
        runCatching {
            userRepository.findByUserId(userId)
        }.onFailure {
            // This is normal
            logger.info("User ID with $userId is not found. Granted to create account")
        }.onSuccess {
            // Username Already Exists
            logger.error("UserID with $userId seems like already exists!")
            throw ConflictException("Username $userId already exists!")
        }
    }

    fun registerUser(registerRequest: RegisterRequest): RegisterResponse {
        // Check whether user account exists
        isUserAccountExists(registerRequest.userId)

        // Register!
        val responseUser: User = userRepository.addUser(
            registerRequest.toUser()
        )

        return RegisterResponse(
            registeredId = responseUser.userId
        )
    }

    fun loginUser(loginRequest: LoginRequest): LoginResponse {
        val requestedUser: User = userRepository.findByUserId(loginRequest.userId)

        if (requestedUser.userPassword != loginRequest.userPassword) {
            throw ForbiddenException("Password is wrong!")
        }

        return LoginResponse(
            userToken = jwtTokenProvider.createToken(requestedUser.userId, requestedUser.roles.toList())
        )
    }

    fun userReserveSeat(seatSelectRequest: SeatSelectRequest, userToken: String): UserLeftTimeResponse {
        val user: User = findUserByToken(userToken).apply {
            // Reserve and get Seat Number
            this.reservedSeatNumber = seatService.reserveSeat(this, seatSelectRequest.seatNumber).toString()
            initUserTimer(this)
        }

        // TODO: on this request, client need to connect websocket and get its data

        // Since we have Object ID Field, template will replace[update] object on DB
        val savedUser: User = userRepository.addUser(user)
        return UserLeftTimeResponse(
            reservedSeat = SeatSelectResponse(savedUser.reservedSeatNumber.toInt()),
            leftTime = savedUser.leftTime
        )
    }

    fun userChangeSeat(seatSelectRequest: SeatSelectRequest, userToken: String): Int {
        val user: User = findUserByToken(userToken).apply {
            this.reservedSeatNumber = seatService.changeSeat(this, seatSelectRequest.seatNumber).toString()
        }

        return userRepository.addUser(user).reservedSeatNumber.toInt()
    }

    fun userChangeState(stateChangeRequest: StateChangeRequest, userToken: String) {
        val user: User = findUserByToken(userToken)
        /**
         * We need to notify realtime server to stop timer, and save remaining time to DB & its new state
         */
        when (stateChangeRequest.userState.toUpperCase()) {
            UserState.BREAK.name -> {
                // Stop timer
                // If an hour passed - make them 'exit'
            }
            UserState.EXIT.name -> {
                // Stop timer
                // Null-fy current seat
            }
            UserState.START.name -> {
                // Restart Timer
            }
            else -> {
                throw NotFoundException("${stateChangeRequest.userState.toUpperCase()} is not found!")
            }
        }
    }

    fun userReport(reportRequest: ReportRequest, userToken: String) {
        val user: User = findUserByToken(userToken)
        // do stuff
    }

    fun userExtendTime(userToken: String) {
        val user: User = findUserByToken(userToken)
        // Update time on Realtime server
    }

    private fun initUserTimer(user: User) {
        val defaultLetTimer: Long = 60 * 60 * 3
        user.startTime = Instant.now().epochSecond
        user.endTime = user.startTime + defaultLetTimer
        user.leftTime = user.endTime - Instant.now().epochSecond
    }
}