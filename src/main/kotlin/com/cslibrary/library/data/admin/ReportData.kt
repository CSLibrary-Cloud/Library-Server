package com.cslibrary.library.data.admin

import com.cslibrary.library.data.dto.request.ReportRequest
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="report")
data class ReportData(
    @Id
    var id: ObjectId = ObjectId(),
    var reportUserId: String, // Who reported - as ID
    var reportContent: ReportRequest,
    var isReportHandlingDone: Boolean = false
)