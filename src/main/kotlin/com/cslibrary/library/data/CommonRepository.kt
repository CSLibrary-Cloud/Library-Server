package com.cslibrary.library.data

import com.cslibrary.library.error.exception.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

open class CommonRepository(
    @PublishedApi
    internal val mongoTemplate: MongoTemplate
) {
    // Logger
    protected open val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // For finding
    protected inline fun<reified T> findOneByQuery(fieldName: String, fieldTargetValue: String): T {
        // Create MongoDB Query
        val query: Query = Query().apply {
            addCriteria(
                Criteria.where(fieldName).`is`(fieldTargetValue)
            )
        }

        // Find it
        return mongoTemplate.findOne(query, T::class.java) ?: run {
            logger.error("User Data is is not found!")
            logger.error("Query: $query")
            throw NotFoundException("Userdata is not found!")
        }
    }

    protected inline fun<reified T> findAllByQuery(fieldName: String, fieldTargetValue: String): List<T> {
        // Create MongoDB Query
        val query: Query = Query().apply {
            addCriteria(
                Criteria.where(fieldName).`is`(fieldTargetValue)
            )
        }

        // Find it
        return mongoTemplate.find(query, T::class.java)
    }

    protected inline fun<reified T> addOrUpdateEntity(entity: T): T {
        return mongoTemplate.save(entity!!)
    }

    protected inline fun<reified T> findAll(): List<T> = mongoTemplate.findAll()
}