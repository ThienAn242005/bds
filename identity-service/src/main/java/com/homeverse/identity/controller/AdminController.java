package com.homeverse.identity.controller;

import com.homeverse.common.dto.ApiResponse;
import com.homeverse.identity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/{id}/status")
    public ApiResponse<String> toggleUserStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ApiResponse.<String>builder()
                .result("Đã thay đổi trạng thái hoạt động của tài khoản thành công.")
                .build();
    }

    @PutMapping("/{id}/promote")
    public ApiResponse<String> promoteToAdmin(@PathVariable Long id) {
        adminService.promoteToAdmin(id);
        return ApiResponse.<String>builder()
                .result("Đã cấp quyền Quản trị viên (ADMIN) cho tài khoản này.")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ApiResponse.<String>builder()
                .result("Đã xóa vĩnh viễn tài khoản khỏi hệ thống.")
                .build();
    }
}