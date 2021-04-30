package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.dto.request.RegisterRequest
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

    @Test
    fun is_registerUser_throws_IllegalStateException_duplicated_id() {
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
            assertThat(it is IllegalStateException).isEqualTo(true)
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
}