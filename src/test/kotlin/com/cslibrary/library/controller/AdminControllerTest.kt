package com.cslibrary.library.controller

import com.cslibrary.library.config.AdminConfig
import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.admin.ReportData
import com.cslibrary.library.data.dto.request.LoginRequest
import com.cslibrary.library.data.dto.request.RegisterRequest
import com.cslibrary.library.service.PasswordEncryptorService
import com.cslibrary.library.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class AdminControllerTest {
    @LocalServerPort
    private var port: Int? = null

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncryptorService: PasswordEncryptorService

    private lateinit var baseUrl: String

    private fun getHttpHeader(userToken: String) = HttpHeaders().apply {
        put("X-AUTH-TOKEN", listOf(userToken))
    }

    fun adminLoginToken(): String {
        userRepository.addUser(
            User(
                userId = "admin",
                userPassword = passwordEncryptorService.encodePlainText("testPassword"),
                userName = "admin",
                userPhoneNumber = "",
                roles = setOf("ROLE_ADMIN")
            )
        )

        return userService.loginUser(
            LoginRequest(
                userId = "admin",
                userPassword = "testPassword"
            )
        ).userToken
    }

    @Before
    @After
    fun initTest() {
        baseUrl = "http://localhost:${port}"
        mongoTemplate.remove(Query(), User::class.java)
        mongoTemplate.remove(Query(), ReportData::class.java)
    }

    private val mockUserRequest: RegisterRequest = RegisterRequest(
        userId = "KangDroid",
        userPassword = "testPassword"
    )

    @Test
    fun is_banning_works_well() {
        val loginToken: String = adminLoginToken()
        userRepository.addUser(mockUserRequest.toUser())
        val uri: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/api/v1/admin/user/${mockUserRequest.userId}")
            .queryParam("ban", true)

        val response: ResponseEntity<Unit> =
            restTemplate.exchange(
                uri.toUriString(), HttpMethod.PUT, HttpEntity<Unit>(getHttpHeader(loginToken))
            )

        // Is Request Succeed?
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)

        // Is user is correctly banned?
        val user: User = userRepository.findByUserId(mockUserRequest.userId)
        assertThat(user.userNonBanned).isEqualTo(false)
    }

    @Test
    fun is_unban_works_well() {
        val loginToken: String = adminLoginToken()
        // Hardcode Ban User
        val banUser: User = userRepository.addUser(mockUserRequest.toUser()).apply {
            userNonBanned = false
        }

        // Ban User Save
        userRepository.addUser(banUser)
        val uri: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/api/v1/admin/user/${mockUserRequest.userId}")
            .queryParam("ban", false)

        // Try to unban
        val response: ResponseEntity<Unit> =
            restTemplate.exchange(
                uri.toUriString(), HttpMethod.PUT, HttpEntity<Unit>(getHttpHeader(loginToken))
            )

        // Is Request Succeed?
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)

        // Is user is correctly banned?
        val user: User = userRepository.findByUserId(mockUserRequest.userId)
        assertThat(user.userNonBanned).isEqualTo(true)
    }
}