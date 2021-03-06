package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserNotification
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.admin.ReportData
import com.cslibrary.library.data.admin.ReportRepository
import com.cslibrary.library.data.dto.request.NotifyUserRequest
import com.cslibrary.library.data.dto.request.RegisterRequest
import com.cslibrary.library.data.dto.request.ReportRequest
import com.cslibrary.library.data.dto.response.SealedUser
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
class AdminServiceTest {
    @Autowired
    private lateinit var adminService: AdminService

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var reportRepository: ReportRepository

    @Before
    @After
    fun initTest() {
        mongoTemplate.remove(Query(), ReportData::class.java)
        mongoTemplate.remove(Query(), User::class.java)
    }

    @Test
    fun is_banUser_works_well() {
        userService.registerUser(
            RegisterRequest(
                userId = "KangDroid",
                userPassword = "test"
            )
        )

        val response: User = adminService.banUser("KangDroid")

        assertThat(response.userNonBanned).isEqualTo(false)
        assertThat(response.isAccountNonLocked).isEqualTo(false)
    }

    @Test
    fun is_unbanUser_works_well() {
        userService.registerUser(
            RegisterRequest(
                userId = "KangDroid",
                userPassword = "test"
            )
        )

        val response: User = adminService.unbanUser("KangDroid")

        assertThat(response.userNonBanned).isEqualTo(true)
        assertThat(response.isAccountNonLocked).isEqualTo(true)
    }

    @Test
    fun is_getAllReport_works_well() {
        val response: List<ReportData> = adminService.getAllReport()
        assertThat(response.isEmpty()).isEqualTo(true)
    }

    @Test
    fun is_dismissReport_works_well() {
        val reportData: ReportData = ReportData(
            reportUserId = "KangDroid",
            reportContent = ReportRequest(
                reportMessage = "Test Content"
            )
        )
        // Insert First
        val report: ReportData = reportRepository.addReportData(reportData)

        adminService.dismissReport(report.reportIdentifier)

        assertThat(reportRepository.findAllReportData().isEmpty()).isEqualTo(true)

    }

    @Test
    fun is_getAllUser_works_well() {
        userService.registerUser(
            RegisterRequest(
                userId = "KangDroid",
                userPassword = "test"
            )
        )
        val userEmptyList: List<SealedUser> = adminService.getAllUser()
        assertThat(userEmptyList.isEmpty()).isEqualTo(false)
        assertThat(userEmptyList.size).isEqualTo(1)
        assertThat(userEmptyList[0].userId).isEqualTo("KangDroid")
    }

    @Test
    fun is_notifyUser_works_well() {
        val mockUserNotification: UserNotification = UserNotification(
            notificationTitle = "Test Title",
            notificationMessage = "Test Content"
        )

        userService.registerUser(
            RegisterRequest(
                userId = "KangDroid",
                userPassword = "test"
            )
        )

        adminService.notifyUser(
            NotifyUserRequest(
                userId = "KangDroid",
                userNotification = mockUserNotification
            )
        )

        val user: User = userRepository.findByUserId("KangDroid")
        assertThat(user.userNotificationList.isEmpty()).isEqualTo(false)
        assertThat(user.userNotificationList[0].notificationTitle).isEqualTo(mockUserNotification.notificationTitle)
        assertThat(user.userNotificationList[0].notificationMessage).isEqualTo(mockUserNotification.notificationMessage)
    }
}