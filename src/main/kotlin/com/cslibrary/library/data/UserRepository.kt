package com.cslibrary.library.data

import com.cslibrary.library.error.exception.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val mongoTemplate: MongoTemplate // DI Injection
) {
    // Logger
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // Field - TODO: Probably we can use Java/Kotlin Reflection to get this?
    private val userStateField: String = "userState"
    private val userSeatNumberField: String = "reservedSeatNumber"
    private val userPhoneNumberField: String = "userPhoneNumber"
    private val userNameField: String = "userName"
    private val userIdField: String = "userId"

    // For finding
    private fun findOneByQuery(fieldName: String, fieldTargetValue: String): User {
        // Create MongoDB Query
        val query: Query = Query().apply {
            addCriteria(
                Criteria.where(fieldName).`is`(fieldTargetValue)
            )
        }

        // Find it
        return mongoTemplate.findOne(query, User::class.java) ?: run {
            logger.error("User Data is is not found!")
            logger.error("Query: $query")
            throw NotFoundException("Userdata is not found!")
        }
    }

    private fun findAllByQuery(fieldName: String, fieldTargetValue: String): List<User> {
        // Create MongoDB Query
        val query: Query = Query().apply {
            addCriteria(
                Criteria.where(fieldName).`is`(fieldTargetValue)
            )
        }

        // Find it
        return mongoTemplate.find(query, User::class.java)
    }

    fun addUser(user: User): User {
        return mongoTemplate.save(user)
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
}