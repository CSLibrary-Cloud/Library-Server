package com.cslibrary.library.data.admin

import com.cslibrary.library.data.CommonRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.stereotype.Repository

@Repository
class ReportRepository(
    mongoTemplate: MongoTemplate
) : CommonRepository(mongoTemplate) {
    // Field
    private val reportUserIdField: String = "reportUserId"
    private val reportIdentifierField: String = "reportIdentifier"
    private val reportContentField: String = "reportContent"
    private val isReportHandlingDoneField: String = "isReportHandlingDone"

    fun findAllReportData(): List<ReportData> = findAll()

    fun findByReporterId(targetId: String): List<ReportData> {
        return findAllByQuery(reportUserIdField, targetId)
    }

    fun findByReportIdentifier(targetIdentifier: String): ReportData {
        return findOneByQuery(reportIdentifierField, targetIdentifier)
    }

    fun addReportData(reportData: ReportData): ReportData {
        return addOrUpdateEntity(reportData)
    }
}