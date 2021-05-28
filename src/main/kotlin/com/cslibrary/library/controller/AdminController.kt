package com.cslibrary.library.controller

import com.cslibrary.library.service.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
}