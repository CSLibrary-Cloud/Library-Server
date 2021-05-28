package com.cslibrary.library.data.admin

import com.cslibrary.library.data.CommonRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class ReportRepository(
    mongoTemplate: MongoTemplate
) : CommonRepository(mongoTemplate) {
    // Field
    private val reportUserIdField: String = "reportUserId"
    private val reportContentField: String = "reportContent"
    private val isReportHandlingDoneField: String = "isReportHandlingDone"

    fun findByReporterId(targetId: String): List<ReportData> {
        return findAllByQuery(reportUserIdField, targetId)
    }

    fun addReportData(reportData: ReportData): ReportData {
        return addOrUpdateEntity(reportData)
    }
}