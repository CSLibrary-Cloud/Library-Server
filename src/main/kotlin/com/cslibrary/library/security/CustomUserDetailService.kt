package com.cslibrary.library.security

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.error.exception.UnknownErrorException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(userId: String): UserDetails {
        val user: User = userRepository.findByUserId(userId)
        if (!user.isAccountNonLocked) {
            throw UnknownErrorException("Account seems like locked! Contact Admin for details.")
        }
        return user
    }
}