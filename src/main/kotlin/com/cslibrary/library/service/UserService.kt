package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.dto.request.LoginRequest
import com.cslibrary.library.data.dto.request.RegisterRequest
import com.cslibrary.library.data.dto.response.LoginResponse
import com.cslibrary.library.data.dto.response.RegisterResponse
import com.cslibrary.library.security.JWTTokenProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JWTTokenProvider
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun isUserAccountExists(userId: String) {
        runCatching {
            userRepository.findByUserId(userId)
        }.onFailure {
            // This is normal
            logger.info("User ID with $userId is not found. Granted to create account")
        }.onSuccess {
            // Username Already Exists
            logger.error("UserID with $userId seems like already exists!")
            throw IllegalStateException("Username $userId already exists!")
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
        val requestedUser: User = runCatching {
            userRepository.findByUserId(loginRequest.userId)
        }.getOrElse {
            logger.error("Cannot find user with user id ${loginRequest.userId}")
            logger.error("StackTrace captured on loginUser: ${it.stackTraceToString()}")
            throw it
        }

        if (requestedUser.userPassword != loginRequest.userPassword) {
            throw IllegalArgumentException("Password is wrong!")
        }

        return LoginResponse(
            userToken = jwtTokenProvider.createToken(requestedUser.userId, requestedUser.roles.toList())
        )
    }
}