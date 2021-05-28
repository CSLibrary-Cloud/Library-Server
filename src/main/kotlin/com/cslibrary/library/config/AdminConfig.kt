package com.cslibrary.library.config

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.annotation.PostConstruct

@ConfigurationProperties("cs-admin")
@ConstructorBinding
class AdminConfig(
    private val masterPassword: String
) {

    // Use field injection since we are using constructor as "Configuration"
    @Autowired
    private lateinit var userRepository: UserRepository

    @PostConstruct
    fun registerAdminAccount() {
        userRepository.addUser(
            User(
                userId = "admin",
                userPassword = masterPassword,
                userName = "admin",
                userPhoneNumber = "",
                roles = setOf("ROLE_ADMIN")
            )
        )
    }
}