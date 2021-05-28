package com.cslibrary.library.data

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    mongoTemplate: MongoTemplate // DI Injection
): CommonRepository(mongoTemplate) {
    // Logger
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // Field - TODO: Probably we can use Java/Kotlin Reflection to get this?
    private val userStateField: String = "userState"
    private val userSeatNumberField: String = "reservedSeatNumber"
    private val userPhoneNumberField: String = "userPhoneNumber"
    private val userNameField: String = "userName"
    private val userIdField: String = "userId"
    private val userTotalStudyTimeField: String = "totalStudyTime"

    // Compatibility function
    fun addUser(user: User): User {
        return addOrUpdateEntity(user)
    }

    fun findByUserId(userId: String): User {
        return findOneByQuery(userIdField, userId)
    }

    fun findByUserName(userName: String): User {
        return findOneByQuery(userNameField, userName)
    }

    fun findByUserPhoneNumber(userPhoneNumber: String): User {
        return findOneByQuery(userPhoneNumberField, userPhoneNumber)
    }

    fun findByReservedSeatNumber(reservedSeatNumber: Int): User {
        return findOneByQuery(userSeatNumberField, reservedSeatNumber.toString())
    }

    fun findAllByUserState(userState: UserState): List<User> {
        return findAllByQuery(userStateField, userState.name)
    }

    fun findAllByUserTotalTimeSort(): List<User> {
        val query: Query = Query().apply {
            with(Sort.by(Sort.Direction.DESC, userTotalStudyTimeField))
        }

        return mongoTemplate.find(query)
    }

    fun findAllUser(): List<User> = findAll()
}