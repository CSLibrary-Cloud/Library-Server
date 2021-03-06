package com.cslibrary.library.controller

import com.cslibrary.library.data.dto.request.*
import com.cslibrary.library.data.dto.response.*
import com.cslibrary.library.service.SeatService
import com.cslibrary.library.service.UserService
import org.apache.coyote.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService,
    private val seatService: SeatService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    // Throws: ConflictException when Duplicated UserID Exists
    @PostMapping("/api/v1/user")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<RegisterResponse> {
        val registerResponse: RegisterResponse = userService.registerUser(registerRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(registerResponse)
    }

    // Throws NotFoundException when id does not exists
    // Throws ForbiddenException when password is incorrrect
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

    // Throws ConflictException when duplicated id registered in seat service.
    @PostMapping("/api/v1/seat")
    fun reserveSeat(@RequestHeader header: HttpHeaders, @RequestBody userSeatSelectRequest: SeatSelectRequest): ResponseEntity<UserLeftTimeResponse> {
        val userToken: String = header["X-AUTH-TOKEN"]!![0]

        logger.info("User requested seat reservation: ${userSeatSelectRequest.seatNumber}")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.userReserveSeat(userSeatSelectRequest, userToken))
    }

    @PutMapping("/api/v1/seat")
    fun changeSeat(@RequestHeader header: HttpHeaders, @RequestBody userSeatSelectRequest: SeatSelectRequest): ResponseEntity<SeatSelectResponse> {
        val userToken: String = header["X-AUTH-TOKEN"]!![0]
        val changedSeat: Int = userService.userChangeSeat(userSeatSelectRequest, userToken)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SeatSelectResponse(changedSeat))
    }

    @PutMapping("/api/v1/state")
    fun changeState(@RequestHeader header: HttpHeaders, @RequestBody stateChangeRequest: StateChangeRequest): ResponseEntity<Void> {
        val userToken: String = header["X-AUTH-TOKEN"]!![0]
        // Change State
        userService.userChangeState(stateChangeRequest, userToken)
        return ResponseEntity
            .noContent().build()
    }

    @PostMapping("/api/v1/report")
    fun userReport(@RequestHeader header: HttpHeaders, @RequestBody userReportRequest: ReportRequest): ResponseEntity<Void> {
        val userToken: String = header["X-AUTH-TOKEN"]!![0]
        userService.userReport(userReportRequest, userToken)
        return ResponseEntity
            .noContent().build()
    }

    @PostMapping("/api/v1/user/time")
    fun saveLeftTime(@RequestHeader header: HttpHeaders, @RequestBody saveLeftTime: SaveLeftTime): ResponseEntity<SaveLeftTimeResponse> {
        logger.info("User requested to save left time: ${saveLeftTime.leftTime}")
        val userToken: String = header["X-AUTH-TOKEN"]!![0]

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                userService.userSaveLeftTime(userToken, saveLeftTime)
            )
    }

    @PostMapping("/api/v1/user/time/extend")
    fun extendTime(@RequestHeader httpHeaders: HttpHeaders, @RequestBody extendTimeRequest: ExtendTimeRequest): ResponseEntity<ExtendTimeResponse> {
        val userToken: String = httpHeaders["X-AUTH-TOKEN"]!![0]
        return ResponseEntity.ok(
            userService.extendUserTime(userToken, extendTimeRequest.leftTime)
        )
    }
}