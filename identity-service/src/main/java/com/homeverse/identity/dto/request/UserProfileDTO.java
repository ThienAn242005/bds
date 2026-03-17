package com.homeverse.identity.dto.request;
import com.homeverse.identity.model.json.LifestyleProfile;
import lombok.Data;

@Data
public class UserProfileDTO {
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String citizenId;
    private String bannerUrl;
    private LifestyleProfile lifestyleProfile;
}