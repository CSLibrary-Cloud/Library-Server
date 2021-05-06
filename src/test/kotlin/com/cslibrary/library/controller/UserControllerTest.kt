package com.cslibrary.library.controller

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.dto.request.LoginRequest
import com.cslibrary.library.data.dto.request.RegisterRequest
import com.cslibrary.library.data.dto.response.LoginResponse
import com.cslibrary.library.data.dto.response.RegisterResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
}