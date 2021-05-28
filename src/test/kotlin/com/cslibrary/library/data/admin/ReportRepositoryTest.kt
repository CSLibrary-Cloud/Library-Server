package com.cslibrary.library.data.admin

import com.cslibrary.library.data.User
import com.cslibrary.library.data.dto.request.ReportRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class ReportRepositoryTest {
    @Autowired
    private lateinit var reportRepository: ReportRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Before
    @After
    fun initTest() {
        mongoTemplate.remove(Query(), ReportData::class.java)
    }

    @Test
    fun is_findByReporterId_works_well() {
        val reportData: ReportData = ReportData(
            reportUserId = "KangDroid",
            reportContent = ReportRequest(
                reportMessage = "Test Content"
            )
        )
        // Insert First
        val report: ReportData = reportRepository.addReportData(reportData)

        // Find Value
        val reportList: List<ReportData> = reportRepository.findByReporterId("KangDroid")
        assertThat(reportList.size).isEqualTo(1)
        assertThat(reportList[0].reportContent.reportMessage).isEqualTo(reportData.reportContent.reportMessage)
    }

    @Test
    fun is_findAll_works_well() {
        val reportData: ReportData = ReportData(
            reportUserId = "KangDroid",
            reportContent = ReportRequest(
                reportMessage = "Test Content"
            )
        )
        // Insert First
        val report: ReportData = reportRepository.addReportData(reportData)

        // Find Value
        val reportList: List<ReportData> = reportRepository.findAllReportData()
        assertThat(reportList.size).isEqualTo(1)
        assertThat(reportList[0].reportContent.reportMessage).isEqualTo(reportData.reportContent.reportMessage)
    }

}