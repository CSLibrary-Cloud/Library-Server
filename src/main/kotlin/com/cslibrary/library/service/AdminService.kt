package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.admin.ReportData
import com.cslibrary.library.data.admin.ReportRepository
import com.cslibrary.library.data.dto.response.SealedUser
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository
) {
    fun banUser(userId: String): User {
        val user: User = userRepository.findByUserId(userId).apply {
            userNonBanned = false
        }

        return userRepository.addUser(user)
    }

    fun unbanUser(userId: String): User {
        val user: User = userRepository.findByUserId(userId).apply {
            userNonBanned = true
        }

        return userRepository.addUser(user)
    }

    fun getAllReport(): List<ReportData> = reportRepository.findAllReportData()

    fun dismissReport(customId: String) {
        reportRepository.deleteByCustomId(customId)
    }

    fun getAllUser(): List<SealedUser> = userRepository.findAllUser().map {
        SealedUser(
            userId = it.userId,
            userName = it.userName,
            userPhoneNumber = it.userPhoneNumber,
            userNonBanned = it.userNonBanned,
            userState = it.userState,
            leftTime = it.leftTime,
            totalStudyTime = it.totalStudyTime,
            reservedSeatNumber = it.reservedSeatNumber
        )
    }
}