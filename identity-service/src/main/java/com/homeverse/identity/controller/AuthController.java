package com.homeverse.identity.controller;

import com.homeverse.common.dto.ApiResponse;
import com.homeverse.identity.dto.request.LoginDTO;
import com.homeverse.identity.dto.request.UserRegisterDTO;
import com.homeverse.identity.dto.response.AuthResponse;
import com.homeverse.identity.dto.response.UserResponseDTO;
import com.homeverse.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // Đã bỏ /api vì API Gateway sẽ lo việc định tuyến
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserResponseDTO> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        return ApiResponse.<UserResponseDTO>builder()
                .result(userService.register(registerDTO))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ApiResponse.<AuthResponse>builder()
                .result(userService.login(loginDTO))
                .build();
    }
}