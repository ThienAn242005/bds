package com.homeverse.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInitDTO {
    private Long id; // Bắt buộc phải có để đồng bộ 1-1 với identity-service
    private String email;
    private String fullName;
    private String phone;
}