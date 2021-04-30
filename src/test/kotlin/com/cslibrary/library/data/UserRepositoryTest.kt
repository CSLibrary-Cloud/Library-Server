package com.cslibrary.library.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

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
}