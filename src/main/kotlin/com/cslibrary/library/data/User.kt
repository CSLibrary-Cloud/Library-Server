package com.cslibrary.library.data

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

enum class UserState {
    BREAK, EXIT, START
}

@Document(collection="user")
data class User(
    @Id
    var id: ObjectId = ObjectId(),
    var userId: String = "",
    var userPassword: String = "",
    var userName: String = "",
    var userPhoneNumber: String = "",
    var roles: Set<String> = setOf(),
    var startTime: Long = -1,
    var leftTime: Long = -1,
    var endTime: Long = -1,
    var reservedSeatNumber: String = "",
    var userState: UserState = UserState.EXIT
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return roles.stream()
            .map { role: String? ->
                SimpleGrantedAuthority(
                    role
                )
            }
            .collect(Collectors.toList())
    }

    override fun getPassword() = userPassword
    override fun getUsername(): String = userName
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}