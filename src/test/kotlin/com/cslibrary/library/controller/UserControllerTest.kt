package com.cslibrary.library.controller

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.dto.request.LoginRequest
import com.cslibrary.library.data.dto.request.RegisterRequest
import com.cslibrary.library.data.dto.request.SeatSelectRequest
import com.cslibrary.library.data.dto.response.LoginResponse
import com.cslibrary.library.data.dto.response.RegisterResponse
import com.cslibrary.library.data.dto.response.SeatResponse
import com.cslibrary.library.data.dto.response.SeatSelectResponse
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

        val seatSelectResponse: ResponseEntity<SeatSelectResponse> =
            restTemplate.exchange(serverBaseUrl, HttpMethod.POST, HttpEntity<SeatSelectRequest>(SeatSelectRequest(5), httpHeader))

        assertThat(seatSelectResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(seatSelectResponse.body).isNotEqualTo(null)
        assertThat(seatSelectResponse.body!!.reservedSeatNumber).isEqualTo(5)
    }
}