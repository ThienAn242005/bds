package com.homeverse.customer.service;

import com.homeverse.customer.dto.request.CustomerInitDTO;
import com.homeverse.customer.dto.request.CustomerProfileDTO;
import com.homeverse.customer.dto.request.KycRequestDTO;
import com.homeverse.customer.dto.response.CustomerResponseDTO;

public interface CustomerService {

    /**
     * Hàm này được gọi bởi Identity Service (thông qua Kafka hoặc OpenFeign)
     * để khởi tạo một hồ sơ rỗng khi có một user mới vừa đăng ký thành công.
     */
    void initCustomerProfile(CustomerInitDTO dto);

    /**
     * Lấy thông tin hồ sơ của người dùng hiện tại (dựa vào Token).
     */
    CustomerResponseDTO getMyProfile();

    /**
     * Cập nhật thông tin cá nhân của người dùng hiện tại.
     */
    CustomerResponseDTO updateProfile(CustomerProfileDTO dto);

    /**
     * Gửi yêu cầu định danh (KYC) với giấy tờ tùy thân.
     */
    void submitKyc(KycRequestDTO dto);
}