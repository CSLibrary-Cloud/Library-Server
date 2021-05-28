package com.cslibrary.library.controller

import com.cslibrary.library.data.admin.ReportData
import com.cslibrary.library.data.dto.request.NotifyUserRequest
import com.cslibrary.library.data.dto.response.SealedUser
import com.cslibrary.library.service.AdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AdminController(
    private val adminService: AdminService
) {
    @PutMapping("/api/v1/admin/user/{id}")
    fun updateUser(@PathVariable("id") userId: String, @RequestParam(value = "ban") isBan: Boolean): ResponseEntity<Unit> {
        if (isBan) {
            adminService.banUser(userId)
        } else {
            adminService.unbanUser(userId)
        }
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/api/v1/admin/report")
    fun getUserReport(): ResponseEntity<List<ReportData>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.getAllReport())
    }

    @DeleteMapping("/api/v1/admin/report/{reportId}")
    fun removeUserReport(@PathVariable("reportId") customId: String): ResponseEntity<Unit> {
        adminService.dismissReport(customId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/api/v1/admin/user")
    fun getUserInformation(): ResponseEntity<List<SealedUser>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(adminService.getAllUser())
    }

    @PostMapping("/api/v1/admin/user/notification")
    fun postNotificationToUser(@RequestBody notifyUserRequest: NotifyUserRequest): ResponseEntity<Unit> {
        adminService.notifyUser(notifyUserRequest)
        return ResponseEntity.noContent().build()
    }
}