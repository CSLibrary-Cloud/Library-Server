package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.UserRepository
import com.cslibrary.library.data.admin.ReportData
import com.cslibrary.library.data.admin.ReportRepository
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
}