package com.cslibrary.library.controller

import com.cslibrary.library.data.dto.request.LoginRequest
import com.cslibrary.library.data.dto.request.RegisterRequest
import com.cslibrary.library.data.dto.response.LoginResponse
import com.cslibrary.library.data.dto.response.RegisterResponse
import com.cslibrary.library.data.dto.response.SeatResponse
import com.cslibrary.library.service.SeatService
import com.cslibrary.library.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService,
    private val seatService: SeatService
) {
    @PostMapping("/api/v1/user")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<RegisterResponse> {
        val registerResponse: RegisterResponse = userService.registerUser(registerRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(registerResponse)
    }

    @PostMapping("/api/v1/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResponse: LoginResponse = userService.loginUser(loginRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(loginResponse)
    }

    @GetMapping("/api/v1/seat")
    fun getSeatInfo(@RequestHeader header: HttpHeaders): ResponseEntity<List<SeatResponse>> {
        val seatResponse: List<SeatResponse> = seatService.getAllSeats()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(seatResponse)
    }
}