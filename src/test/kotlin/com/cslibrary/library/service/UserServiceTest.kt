package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.dto.request.LoginRequest
import com.cslibrary.library.data.dto.request.RegisterRequest
import com.cslibrary.library.data.dto.request.SeatSelectRequest
import com.cslibrary.library.error.exception.ConflictException
import com.cslibrary.library.error.exception.ForbiddenException
import com.cslibrary.library.error.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit4.SpringRunner
import java.lang.NullPointerException
import java.lang.reflect.Method

@RunWith(SpringRunner::class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Before
    @After
    fun initTest() {
        mongoTemplate.remove(Query(), User::class.java)
    }

    private fun initMockUser(): String {
        // Before starting, add mock user first
        val mockUserId: String = "KangDroid"
        mongoTemplate.save(
            User(
                userId = mockUserId
            )
        )

        return userService.loginUser(LoginRequest(
            mockUserId, ""
        )).userToken
    }

    @Test
    fun is_registerUser_throws_ConflictException_duplicated_id() {
        // Before starting, add mock user first
        val mockUserId: String = "KangDroid"
        mongoTemplate.save(
            User(
                userId = mockUserId
            )
        )

        val mockUserRequest: RegisterRequest = RegisterRequest(userId = mockUserId)

        runCatching {
            userService.registerUser(mockUserRequest)
        }.onSuccess {
            fail("We have registered duplicated ID and it succeed?")
        }.onFailure {
            val userList: List<User> = mongoTemplate.findAll(User::class.java)
            assertThat(it is ConflictException).isEqualTo(true)
            assertThat(userList.size).isEqualTo(1)
        }
    }

    @Test
    fun is_registerUser_works_well() {
        // Before starting, add mock user first
        val mockUserId: String = "KangDroid"

        val mockUserRequest: RegisterRequest = RegisterRequest(userId = mockUserId)

        runCatching {
            userService.registerUser(mockUserRequest)
        }.onFailure {
            println(it.stackTraceToString())
            fail("DB is empty, but somehow it failed to register user.")
        }.onSuccess {
            val userList: List<User> = mongoTemplate.findAll(User::class.java)
            assertThat(userList.size).isEqualTo(1)
            assertThat(userList[0].userId).isEqualTo(mockUserId)
        }
    }

    @Test
    fun is_loginUser_throws_NotFoundException_no_user() {
        val mockUserId: String = "KangDroid"

        runCatching {
            userService.loginUser(
                LoginRequest(
                    userId = mockUserId
                )
            )
        }.onSuccess {
            fail("We do not have user called $mockUserId and it succeeds to login?")
        }.onFailure {
            println(it.stackTraceToString())
            assertThat(it is NotFoundException).isEqualTo(true)
        }
    }

    @Test
    fun is_loginUser_throws_IllegalArgumentException_password_wrong() {
        val mockUserId: String = "KangDroid"
        mongoTemplate.save(
            User(
                userId = mockUserId
            )
        )

        runCatching {
            userService.loginUser(
                LoginRequest(
                    userId = mockUserId,
                    userPassword = "ahhh, no"
                )
            )
        }.onSuccess {
            fail("Password should be wrong, but succeeds?")
        }.onFailure {
            println(it.stackTraceToString())
            assertThat(it is ForbiddenException).isEqualTo(true)
            assertThat(it.message).isEqualTo("Password is wrong!")
        }
    }

    @Test
    fun is_loginUser_works_well() {
        val mockUserId: String = "KangDroid"
        mongoTemplate.save(
            User(
                userId = mockUserId
            )
        )

        runCatching {
            userService.loginUser(
                LoginRequest(
                    userId = mockUserId
                )
            )
        }.onSuccess {
            assertThat(it.userToken).isNotEqualTo("")
        }.onFailure {
            println(it.stackTraceToString())
            fail("All things are good, but it failed to login")
        }
    }

    @Test
    fun is_findUserByToken_returns_user_well() {
        val method: Method = UserService::class.java.getDeclaredMethod("findUserByToken", String::class.java).apply {
            isAccessible = true
        }
        val targetToken: String = initMockUser()
        val user: User = method.invoke(userService, targetToken) as User
        assertThat(user.userId).isEqualTo("KangDroid")
    }

    @Test
    fun is_findUserByToken_throws_exception() {
        val method: Method = UserService::class.java.getDeclaredMethod("findUserByToken", String::class.java).apply {
            isAccessible = true
        }
        runCatching {
            val user: User = method.invoke(userService, "targetToken") as User
        }.onSuccess {
            fail("We do not have user info, but finding user succeed?")
        }
    }

    @Test
    fun is_userReserveSeat_works_well() {
        val loginToken: String = initMockUser()
        val userSeatNumber: Int = userService.userReserveSeat(SeatSelectRequest(10), loginToken)

        assertThat(userSeatNumber).isEqualTo(10)
    }
}