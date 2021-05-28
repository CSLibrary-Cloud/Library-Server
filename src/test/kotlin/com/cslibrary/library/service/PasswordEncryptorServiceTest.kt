package com.cslibrary.library.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class PasswordEncryptorServiceTest {
    @Autowired
    private lateinit var passwordEncryptorService: PasswordEncryptorService

    private val targetString: String = "Test String!"

    @Test
    fun is_encodePlainText_works_well() {
        val encodedString: String = passwordEncryptorService.encodePlainText(targetString)
        assertThat(targetString).isNotEqualTo(encodedString)
    }

    @Test
    fun is_isMatching_returns_true_correct() {
        val encodedString: String = passwordEncryptorService.encodePlainText(targetString)
        assertThat(passwordEncryptorService.isMatching(targetString, encodedString)).isEqualTo(true)
    }

    @Test
    fun is_isMatching_returns_false_wrong_password() {
        val encodedString: String = passwordEncryptorService.encodePlainText(targetString)
        assertThat(passwordEncryptorService.isMatching("targetString", encodedString)).isEqualTo(false)
    }
}