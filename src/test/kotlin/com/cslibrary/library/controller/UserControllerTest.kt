package com.cslibrary.library.controller

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.dto.request.*
import com.cslibrary.library.data.dto.response.*
import com.cslibrary.library.error.ErrorResponse
import com.cslibrary.library.service.SeatService
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var seatService: SeatService

    private fun getLoginToken(): String {
        val serverBaseUrl: String = "http://localhost:${port}"
        val mockUser: User = RegisterRequest(
            userId = "kangdroid",
            userPassword = "test",
            userName = "testingname",
            userPhoneNumber = "whatever"
        ).toUser()

        val mockLoginRequest: LoginRequest = LoginRequest(
            userId = mockUser.userId,
            userPassword = mockUser.userPassword
        )
        userRepository.addUser(mockUser)

        val loginResponse: ResponseEntity<LoginResponse> =
            restTemplate.postForEntity("${serverBaseUrl}/api/v1/login", mockLoginRequest)

        return loginResponse.body!!.userToken
    }

    @Before
    @After
    fun destroy() {
        mongoTemplate.remove(Query(), User::class.java)
        seatService.initSeats()
    }

    @Test
    fun is_registerUserWorksWell() {
        val serverBaseUrl: String = "http://localhost:${port}"
        val mockUserRegisterRequest: RegisterRequest = RegisterRequest(
            userId = "kangdroid",
            userPassword = "test",
            userName = "testingname",
            userPhoneNumber = "whatever"
        )
        val registerResponse: ResponseEntity<RegisterResponse> =
            restTemplate.postForEntity("${serverBaseUrl}/api/v1/user", mockUserRegisterRequest)

        assertThat(registerResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(registerResponse.hasBody()).isEqualTo(true)
        assertThat(registerResponse.body).isNotEqualTo(null)
        assertThat(registerResponse.body!!.registeredId).isEqualTo(mockUserRegisterRequest.userId)
    }

    @Test
    fun is_registerUser_throws_conflictException() {
        val serverBaseUrl: String = "http://localhost:${port}"
        val mockUserRegisterRequest: RegisterRequest = RegisterRequest(
            userId = "kangdroid",
            userPassword = "test",
            userName = "testingname",
            userPhoneNumber = "whatever"
        )
        userRepository.addUser(mockUserRegisterRequest.toUser())

        val registerResponse: ResponseEntity<ErrorResponse> =
            restTemplate.postForEntity("${serverBaseUrl}/api/v1/user", mockUserRegisterRequest)

        assertThat(registerResponse.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(registerResponse.hasBody()).isEqualTo(true)
        assertThat(registerResponse.body).isNotEqualTo(null)
        assertThat(registerResponse.body!!.errorMessage).isNotEqualTo("")
    }

    @Test
    fun is_loginUserWorksWell() {
        val serverBaseUrl: String = "http://localhost:${port}"
        val mockUser: User = User(
            userId = "kangdroid",
            userPassword = "test",
            userName = "testingname",
            userPhoneNumber = "whatever"
        )
        val mockLoginRequest: LoginRequest = LoginRequest(
            userId = mockUser.userId,
            userPassword = mockUser.userPassword
        )
        userRepository.addUser(mockUser)

        val loginResponse: ResponseEntity<LoginResponse> =
            restTemplate.postForEntity("${serverBaseUrl}/api/v1/login", mockLoginRequest)

        assertThat(loginResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(loginResponse.body).isNotEqualTo(null)
        assertThat(loginResponse.body!!.userToken).isNotEqualTo("")
    }

    @Test
    fun is_loginUser_throws_404_no_id() {
        val serverBaseUrl: String = "http://localhost:${port}"
        val mockUser: RegisterRequest = RegisterRequest(
            userId = "kangdroid",
            userPassword = "test",
            userName = "testingname",
            userPhoneNumber = "whatever"
        )
        val mockLoginRequest: LoginRequest = LoginRequest(
            userId = "mockUser.userId",
            userPassword = mockUser.userPassword
        )
        userRepository.addUser(mockUser.toUser())

        val loginResponse: ResponseEntity<ErrorResponse> =
            restTemplate.postForEntity("${serverBaseUrl}/api/v1/login", mockLoginRequest)

        assertThat(loginResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(loginResponse.body).isNotEqualTo(null)
        assertThat(loginResponse.body!!.errorMessage).isNotEqualTo("")
    }

    @Test
    fun is_loginUser_throws_FORBIDDEN_wrong_password() {
        val serverBaseUrl: String = "http://localhost:${port}"
        val mockUser: RegisterRequest = RegisterRequest(
            userId = "kangdroid",
            userPassword = "test",
            userName = "testingname",
            userPhoneNumber = "whatever"
        )
        val mockLoginRequest: LoginRequest = LoginRequest(
            userId = mockUser.userId,
            userPassword = "mockUser.userPassword"
        )
        userRepository.addUser(mockUser.toUser())

        val loginResponse: ResponseEntity<ErrorResponse> =
            restTemplate.postForEntity("${serverBaseUrl}/api/v1/login", mockLoginRequest)

        assertThat(loginResponse.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(loginResponse.body).isNotEqualTo(null)
        assertThat(loginResponse.body!!.errorMessage).isNotEqualTo("")
    }

    @Test
    fun is_gettingSeatInfo_works_well() {
        val serverBaseUrl: String = "http://localhost:${port}/api/v1/seat"
        val loginToken: String = getLoginToken()
        val httpHeader: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", loginToken)
        }
        val seatResponse: ResponseEntity<List<SeatResponse>> =
            restTemplate.exchange(serverBaseUrl, HttpMethod.GET, HttpEntity<Void>(httpHeader))

        assertThat(seatResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(seatResponse.body).isNotEqualTo(null)
        assertThat(seatResponse.body!!.size).isEqualTo(30)
        // First, seat should not be using at all
        for (eachSeat in seatResponse.body!!) {
            assertThat(eachSeat.isUsing).isEqualTo(false)
        }
    }

    @Test
    fun is_reserving_seat_works_well() {
        val serverBaseUrl: String = "http://localhost:${port}/api/v1/seat"
        val loginToken: String = getLoginToken()
        val httpHeader: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", loginToken)
        }

        val seatSelectResponse: ResponseEntity<UserLeftTimeResponse> =
            restTemplate.exchange(serverBaseUrl, HttpMethod.POST, HttpEntity<SeatSelectRequest>(SeatSelectRequest(5), httpHeader))

        assertThat(seatSelectResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(seatSelectResponse.body).isNotEqualTo(null)
        assertThat(seatSelectResponse.body!!.reservedSeat.reservedSeatNumber).isEqualTo(5)
    }

    @Test
    fun is_reserving_seat_throws_CONFLICT_duplicated_user() {
        val serverBaseUrl: String = "http://localhost:${port}/api/v1/seat"
        val loginToken: String = getLoginToken()
        val httpHeader: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", loginToken)
        }
        restTemplate.exchange<UserLeftTimeResponse>(serverBaseUrl, HttpMethod.POST, HttpEntity<SeatSelectRequest>(SeatSelectRequest(5), httpHeader))

        val seatSelectResponse: ResponseEntity<ErrorResponse> =
            restTemplate.exchange(serverBaseUrl, HttpMethod.POST, HttpEntity<SeatSelectRequest>(SeatSelectRequest(5), httpHeader))

        assertThat(seatSelectResponse.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(seatSelectResponse.body).isNotEqualTo(null)
        assertThat(seatSelectResponse.body!!.errorMessage).isNotEqualTo("")
    }

    @Test
    fun is_changeSeat_works_well() {
        val serverBaseUrl: String = "http://localhost:${port}/api/v1/seat"
        val loginToken: String = getLoginToken()
        val httpHeader: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", loginToken)
        }

        var seatSelectResponse: ResponseEntity<SeatSelectResponse> =
            restTemplate.exchange(serverBaseUrl, HttpMethod.POST, HttpEntity<SeatSelectRequest>(SeatSelectRequest(20), httpHeader))

        assertThat(seatSelectResponse.statusCode).isEqualTo(HttpStatus.OK)
        seatSelectResponse =
            restTemplate.exchange(serverBaseUrl, HttpMethod.PUT, HttpEntity<SeatSelectRequest>(SeatSelectRequest(10), httpHeader))
        assertThat(seatSelectResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(seatSelectResponse.hasBody()).isEqualTo(true)
        assertThat(seatSelectResponse.body!!.reservedSeatNumber).isEqualTo(10)
    }

    @Test
    fun is_changeState_works_well() {
        val serverBaseUrl: String = "http://localhost:${port}/api/v1/state"
        val loginToken: String = getLoginToken()
        val httpHeader: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", loginToken)
        }
        val changeStateResponse: ResponseEntity<Void> =
            restTemplate.exchange(serverBaseUrl, HttpMethod.PUT, HttpEntity<StateChangeRequest>(StateChangeRequest("break"), httpHeader))

        assertThat(changeStateResponse.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun is_userReport_works_well() {
        val serverBaseUrl: String = "http://localhost:${port}/api/v1/report"
        val loginToken: String = getLoginToken()
        val httpHeader: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", loginToken)
        }

        val changeStateResponse: ResponseEntity<Void> =
            restTemplate.exchange(serverBaseUrl, HttpMethod.POST, HttpEntity<ReportRequest>(ReportRequest("break"), httpHeader))

        assertThat(changeStateResponse.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun is_saveLeftTime_works_well() {
        val serverUrl: String = "http://localhost:${port}/api/v1/user/time"
        val loginToken: String = getLoginToken()
        val httpHeader: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", loginToken)
        }

        val changeStateResponse: ResponseEntity<SaveLeftTimeResponse> =
            restTemplate.exchange(serverUrl, HttpMethod.POST, HttpEntity<SaveLeftTime>(SaveLeftTime(5000),httpHeader))

        assertThat(changeStateResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(changeStateResponse.hasBody()).isEqualTo(true)
        assertThat(changeStateResponse.body!!.leaderBoardList.size).isEqualTo(1)
    }
}