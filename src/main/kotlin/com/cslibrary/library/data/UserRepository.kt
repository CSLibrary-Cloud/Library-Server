package com.cslibrary.library.data

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val mongoTemplate: MongoTemplate // DI Injection
) {
    // Field - TODO: Probably we can use Java/Kotlin Reflection to get this?
    private val userStateField: String = "userStateField"
    private val userSeatNumberField: String = "userSeatNumber"
    private val userPhoneNumberField: String = "userPhoneNumber"
    private val userNameField: String = "userName"
    private val userIdField: String = "userId"

    fun addUser(user: User): User {
        return mongoTemplate.insert(user)
    }
}