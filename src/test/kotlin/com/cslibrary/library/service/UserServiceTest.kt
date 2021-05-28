package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.UserState
import com.cslibrary.library.data.dto.LeaderBoard
import com.cslibrary.library.data.dto.request.*
import com.cslibrary.library.data.dto.response.SaveLeftTimeResponse
import com.cslibrary.library.data.dto.response.UserLeftTimeResponse
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
import kotlin.test.fail

@RunWith(SpringRunner::class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var seatService: SeatService

    @Before
    @After
    fun initTest() {
        mongoTemplate.remove(Query(), User::class.java)
        seatService.initSeats()
    }

    private fun initMockUser(): String {
        // Before starting, add mock user first
        val mockUserId: String = "KangDroid"
        userService.registerUser(
            RegisterRequest(
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
            fail("Password should be wrong, but succeeds? ${it.userToken}")
        }.onFailure {
            println("Error: ${it.stackTraceToString()}")
            assertThat(it is ForbiddenException).isEqualTo(true)
            assertThat(it.message).isEqualTo("Password is wrong!")
        }
    }

    @Test
    fun is_loginUser_works_well() {
        val mockUserId: String = "KangDroid"
        userService.registerUser(
            RegisterRequest(
                userId = mockUserId,
                userPassword = "test"
            )
        )

        runCatching {
            userService.loginUser(
                LoginRequest(
                    userId = mockUserId,
                    userPassword = "test"
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
        val userSeatNumber: UserLeftTimeResponse = userService.userReserveSeat(SeatSelectRequest(10), loginToken)

        assertThat(userSeatNumber.reservedSeat.reservedSeatNumber).isEqualTo(10)
    }

    @Test
    fun is_userChangeSeat_works_well() {
        val oldSeat: Int = 10
        val newSeat: Int = 20
        val loginToken: String = initMockUser()
        userService.userReserveSeat(SeatSelectRequest(oldSeat), loginToken)

        val changedSeatNumber: Int = userService.userChangeSeat(SeatSelectRequest(newSeat), loginToken)
        assertThat(changedSeatNumber).isNotEqualTo(oldSeat)
        assertThat(changedSeatNumber).isEqualTo(newSeat)
    }

    @Test
    fun is_userSaveLeftTime_works_well() {
        val loginToken: String = initMockUser()
        // Reserve Seat First
        userService.userReserveSeat(SeatSelectRequest(10), loginToken)

        // Do
        val saveTimeResponse: SaveLeftTimeResponse =
            userService.userSaveLeftTime(loginToken, SaveLeftTime(5000))

        // Find
        val user: User = userRepository.findByUserId("KangDroid")
        assertThat(user.leftTime).isEqualTo(5000)
    }

    @Test
    fun is_getLeaderBoard_works_well() {
        userRepository.addUser(
            User(
                userId = "mockUserId"
            )
        )
        userRepository.addUser(
            User(
                userName = "Test",
                userId = "KangDroid",
                totalStudyTime = 6000
            )
        )

        val list: List<LeaderBoard> = userService.getLeaderBoard()
        assertThat(list.size).isEqualTo(2)
        assertThat(list[0].rank).isEqualTo(1)
        assertThat(list[0].userName).isEqualTo("Test")
        assertThat(list[1].rank).isEqualTo(2)
        assertThat(list[1].userName).isEqualTo("")
    }

    /**
     * Tmp Test for now
     */
    @Test
    fun is_userChangeState_works_well() {
        val loginToken: String = initMockUser()
        userService.userReserveSeat(SeatSelectRequest(10), loginToken)

        userService.userChangeState(StateChangeRequest("break"), loginToken)
        var user: User = userRepository.findByUserId("KangDroid")
        assertThat(user.userState).isEqualTo(UserState.BREAK)

        userService.userChangeState(StateChangeRequest("start"), loginToken)
        user = userRepository.findByUserId("KangDroid")
        assertThat(user.userState).isEqualTo(UserState.START)

        userService.userChangeState(StateChangeRequest("exit"), loginToken)
        user = userRepository.findByUserId("KangDroid")
        assertThat(user.userState).isEqualTo(UserState.EXIT)
    }

    /**
     * Tmp Test for now
     */
    @Test
    fun is_userChangeState_throws_404() {
        val loginToken: String = initMockUser()
        runCatching {
            userService.userChangeState(StateChangeRequest("??"), loginToken)
        }.onSuccess {
            fail("This should not be passed since unknown userstate detected.")
        }.onFailure {
            assertThat(it is NotFoundException).isEqualTo(true)
        }
    }
}