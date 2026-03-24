package com.homeverse.customer.service;

import com.homeverse.customer.dto.request.CustomerInitDTO;
import com.homeverse.customer.dto.request.CustomerProfileDTO;
import com.homeverse.customer.dto.request.KycRequestDTO;
import com.homeverse.customer.dto.response.CustomerResponseDTO;

public interface CustomerService {

    void initCustomerProfile(CustomerInitDTO dto);

    CustomerResponseDTO getMyProfile();

    CustomerResponseDTO updateProfile(CustomerProfileDTO dto);

    void submitKyc(KycRequestDTO dto);
}