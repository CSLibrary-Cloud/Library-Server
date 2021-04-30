package com.cslibrary.library.data

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
    private val userStateField: String = "userStateField"
    private val userSeatNumberField: String = "userSeatNumber"
    private val userPhoneNumberField: String = "userPhoneNumber"
    private val userNameField: String = "userName"
    private val userIdField: String = "userId"

    // For finding
    private fun findOneByQuery(query: Query): User {
        return runCatching {
            mongoTemplate.findOne(query, User::class.java)!!
        }.getOrElse {
            logger.error("Error occurred when getting user data.")
            logger.error("Query: $query")
            logger.error("StackTrace: ${it.stackTraceToString()}")
            throw it
        }
    }

    fun addUser(user: User): User {
        return mongoTemplate.save(user)
    }

    fun findByUserId(userId: String): User {
        val query: Query = Query().apply {
            addCriteria(
                Criteria.where(userIdField).`is`(userId)
            )
        }

        return findOneByQuery(query)
    }

    fun findByUserName(userName: String): User {
        val query: Query = Query().apply {
            addCriteria(
                Criteria.where(userNameField).`is`(userName)
            )
        }

        return findOneByQuery(query)
    }
}