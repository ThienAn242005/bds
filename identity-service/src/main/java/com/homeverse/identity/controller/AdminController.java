package com.homeverse.identity.controller;

import com.homeverse.common.dto.ApiResponse;
import com.homeverse.identity.dto.request.ApproveRequestDTO;
import com.homeverse.identity.dto.response.UserResponseDTO;
import com.homeverse.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users") // Đổi route để phân biệt rõ ràng
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    // Lấy tất cả người dùng
    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        return ApiResponse.<List<UserResponseDTO>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    // Khóa/Mở khóa tài khoản
    @PutMapping("/{id}/status")
    public ApiResponse<String> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ApiResponse.<String>builder()
                .result("Đã thay đổi trạng thái tài khoản!")
                .build();
    }

    // Duyệt hoặc Từ chối hồ sơ KYC
    @PutMapping("/{id}/kyc")
    public ApiResponse<String> approveKYC(@PathVariable Long id, @RequestBody ApproveRequestDTO dto) {
        userService.approveKYC(id, dto);
        return ApiResponse.<String>builder()
                .result("Đã xử lý hồ sơ KYC!")
                .build();
    }

    // Xóa người dùng vĩnh viễn
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<String>builder()
                .result("Đã xóa tài khoản vĩnh viễn!")
                .build();
    }
}