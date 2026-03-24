package com.homeverse.customer.service.impl;

import com.homeverse.customer.dto.request.*;
import com.homeverse.customer.dto.response.CustomerResponseDTO;
import com.homeverse.customer.entity.Customer;
import com.homeverse.customer.repository.CustomerRepository;
import com.homeverse.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    // 1. Dành cho Identity-Service gọi khi có người đăng ký mới
    @Override
    @Transactional
    public void initCustomerProfile(CustomerInitDTO dto) {
        Customer customer = Customer.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .kycStatus("UNVERIFIED")
                .build();
        customerRepository.save(customer);
    }

    // 2. Các hàm lấy thông tin của bản thân
    private Customer getCurrentCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Hồ sơ khách hàng không tồn tại!"));
    }

    @Override
    public CustomerResponseDTO getMyProfile() {
        return modelMapper.map(getCurrentCustomer(), CustomerResponseDTO.class);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateProfile(CustomerProfileDTO dto) {
        Customer customer = getCurrentCustomer();

        if (dto.getFullName() != null) customer.setFullName(dto.getFullName());
        if (dto.getPhone() != null) customer.setPhone(dto.getPhone());
        if (dto.getAvatarUrl() != null) customer.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getBannerUrl() != null) customer.setBannerUrl(dto.getBannerUrl());
        if (dto.getLifestyleProfile() != null) customer.setLifestyleProfile(dto.getLifestyleProfile());

        Customer saved = customerRepository.save(customer);
        return modelMapper.map(saved, CustomerResponseDTO.class);
    }

    @Override
    @Transactional
    public void submitKyc(KycRequestDTO dto) {
        Customer customer = getCurrentCustomer();
        customer.setCitizenId(dto.getCitizenId());
        customer.setCitizenImages(dto.getCitizenImages());
        customer.setKycStatus("PENDING");
        customerRepository.save(customer);


    }
}