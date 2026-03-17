package com.homeverse.identity.controller;

import com.homeverse.common.dto.ApiResponse;
import com.homeverse.identity.dto.request.KycRequestDTO;
import com.homeverse.identity.dto.request.UserProfileDTO;
import com.homeverse.identity.dto.response.UserResponseDTO;
import com.homeverse.identity.entity.User;
import com.homeverse.identity.mapper.UserMapper;
import com.homeverse.identity.repository.UserRepository;
import com.homeverse.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserResponseDTO> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ApiResponse.<UserResponseDTO>builder().result(userMapper.toResponse(user)).build();
    }

    @PutMapping("/profile")
    public ApiResponse<UserResponseDTO> updateProfile(@RequestBody UserProfileDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if(dto.getFullName() != null) user.setFullName(dto.getFullName());
        if(dto.getPhone() != null) user.setPhone(dto.getPhone());
        if(dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if(dto.getBannerUrl() != null) user.setBannerUrl(dto.getBannerUrl());
        if(dto.getCitizenId() != null) user.setCitizenId(dto.getCitizenId());
        if(dto.getLifestyleProfile() != null) user.setLifestyleProfile(dto.getLifestyleProfile());

        return ApiResponse.<UserResponseDTO>builder().result(userMapper.toResponse(userRepository.save(user))).build();
    }

    @PostMapping("/upgrade")
    public ApiResponse<String> upgradeToLandlord() {
        userService.upgradeToLandlord();
        return ApiResponse.<String>builder().result("Nâng cấp thành công! Vui lòng đăng nhập lại.").build();
    }

    @PostMapping("/kyc")
    public ApiResponse<String> submitKyc(@RequestBody @Valid KycRequestDTO dto) {
        userService.submitKyc(dto);
        return ApiResponse.<String>builder().result("Gửi yêu cầu xác minh thành công!").build();
    }
}