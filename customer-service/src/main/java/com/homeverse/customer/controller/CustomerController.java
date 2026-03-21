package com.homeverse.customer.controller;

import com.homeverse.common.dto.ApiResponse;
import com.homeverse.customer.dto.request.*;
import com.homeverse.customer.dto.response.CustomerResponseDTO;
import com.homeverse.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // API nội bộ: Identity-Service sẽ gọi API này
    @PostMapping("/init")
    public ApiResponse<String> initProfile(@RequestBody CustomerInitDTO dto) {
        customerService.initCustomerProfile(dto);
        return ApiResponse.<String>builder().result("Đã khởi tạo hồ sơ rỗng").build();
    }

    @GetMapping("/profile")
    public ApiResponse<CustomerResponseDTO> getMyProfile() {
        return ApiResponse.<CustomerResponseDTO>builder()
                .result(customerService.getMyProfile()).build();
    }

    @PutMapping("/profile")
    public ApiResponse<CustomerResponseDTO> updateProfile(@RequestBody CustomerProfileDTO dto) {
        return ApiResponse.<CustomerResponseDTO>builder()
                .result(customerService.updateProfile(dto)).build();
    }

    @PostMapping("/kyc")
    public ApiResponse<String> submitKyc(@RequestBody @Valid KycRequestDTO dto) {
        customerService.submitKyc(dto);
        return ApiResponse.<String>builder().result("Đã gửi hồ sơ KYC chờ duyệt").build();
    }
}