package com.homeverse.identity.dto.response;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String avatarUrl;
    private String bannerUrl;
    private String kycStatus;
}