package com.cslibrary.library.data.admin

import com.cslibrary.library.data.dto.request.ReportRequest
import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

@Document(collection="report")
data class ReportData(
    @Id
    @JsonIgnore
    var id: ObjectId = ObjectId(),
    var reportUserId: String, // Who reported - as ID
    var reportContent: ReportRequest,
    var isReportHandlingDone: Boolean = false
) {
    var reportIdentifier: String = getSHA512("${reportUserId}.${reportContent.reportMessage}.${System.currentTimeMillis()}.${id}")
    private fun getSHA512(input: String): String {
        val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-512").apply {
            update(input.toByteArray())
        }
        return DatatypeConverter.printHexBinary(messageDigest.digest())
    }
}