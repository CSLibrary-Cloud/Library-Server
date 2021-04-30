package com.cslibrary.library.data

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

@RunWith(SpringRunner::class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Before
    @After
    fun init() {
        // Remove All Data
        mongoTemplate.remove(Query(), User::class.java)
    }

    @Test
    fun is_addUser_works_well() {
        val mockUser: User = User(
            userId = "team2",
            userPassword = "somewhatpassword?",
            userName = "KDR",
            userPhoneNumber = "010-xxxx-xxxx"
        )

        // Do work
        val resultUser: User = userRepository.addUser(mockUser)

        // Assert
        assertThat(resultUser.userId).isEqualTo(mockUser.userId)
        assertThat(resultUser.userPassword).isEqualTo(mockUser.userPassword)
        assertThat(resultUser.userName).isEqualTo(mockUser.userName)
        assertThat(resultUser.userPhoneNumber).isEqualTo(mockUser.userPhoneNumber)
    }

    @Test
    fun is_findByUserId_throws_NullPointerException_no_user() {
        runCatching {
            userRepository.findByUserId("somewhat_userid")
        }.onSuccess {
            fail("We haven't registered user, but it succeeds?")
        }.onFailure {
            println(it.stackTraceToString())
            assertThat(it is NullPointerException).isEqualTo(true)
        }
    }

    @Test
    fun is_findByUserId_works_well() {
        val mockUser: User = User(
            userId = "team2",
            userPassword = "somewhatpassword?",
            userName = "KDR",
            userPhoneNumber = "010-xxxx-xxxx"
        )

        // Register user in db first
        mongoTemplate.save(mockUser)

        // Get User
        val resultUser: User = runCatching {
            userRepository.findByUserId(mockUser.userId)
        }.getOrElse {
            println(it.stackTraceToString())
            fail("We have registered mock user, but somehow finding user failed.")
        }

        // Assert
        assertThat(resultUser.userId).isEqualTo(mockUser.userId)
        assertThat(resultUser.userPassword).isEqualTo(mockUser.userPassword)
        assertThat(resultUser.userName).isEqualTo(mockUser.userName)
        assertThat(resultUser.userPhoneNumber).isEqualTo(mockUser.userPhoneNumber)
    }

    @Test
    fun is_findByUserName_throws_NullPointerException_no_user() {
        runCatching {
            userRepository.findByUserName("somewhat_userid")
        }.onSuccess {
            fail("We haven't registered user, but it succeeds?")
        }.onFailure {
            println(it.stackTraceToString())
            assertThat(it is NullPointerException).isEqualTo(true)
        }
    }

    @Test
    fun is_findByUserName_works_well() {
        val mockUser: User = User(
            userId = "team2",
            userPassword = "somewhatpassword?",
            userName = "KDR",
            userPhoneNumber = "010-xxxx-xxxx"
        )

        // Register user in db first
        mongoTemplate.save(mockUser)

        // Get User
        val resultUser: User = runCatching {
            userRepository.findByUserName(mockUser.userName)
        }.getOrElse {
            println(it.stackTraceToString())
            fail("We have registered mock user, but somehow finding user failed.")
        }

        // Assert
        assertThat(resultUser.userId).isEqualTo(mockUser.userId)
        assertThat(resultUser.userPassword).isEqualTo(mockUser.userPassword)
        assertThat(resultUser.userName).isEqualTo(mockUser.userName)
        assertThat(resultUser.userPhoneNumber).isEqualTo(mockUser.userPhoneNumber)
    }

    @Test
    fun is_findByUserPhoneNumber_throws_NullPointerException_no_user() {
        runCatching {
            userRepository.findByUserPhoneNumber("somewhat_userid")
        }.onSuccess {
            fail("We haven't registered user, but it succeeds?")
        }.onFailure {
            println(it.stackTraceToString())
            assertThat(it is NullPointerException).isEqualTo(true)
        }
    }

    @Test
    fun is_findByUserPhoneNumber_works_well() {
        val mockUser: User = User(
            userId = "team2",
            userPassword = "somewhatpassword?",
            userName = "KDR",
            userPhoneNumber = "010-xxxx-xxxx"
        )

        // Register user in db first
        mongoTemplate.save(mockUser)

        // Get User
        val resultUser: User = runCatching {
            userRepository.findByUserPhoneNumber(mockUser.userPhoneNumber)
        }.getOrElse {
            println(it.stackTraceToString())
            fail("We have registered mock user, but somehow finding user failed.")
        }

        // Assert
        assertThat(resultUser.userId).isEqualTo(mockUser.userId)
        assertThat(resultUser.userPassword).isEqualTo(mockUser.userPassword)
        assertThat(resultUser.userName).isEqualTo(mockUser.userName)
        assertThat(resultUser.userPhoneNumber).isEqualTo(mockUser.userPhoneNumber)
    }
}