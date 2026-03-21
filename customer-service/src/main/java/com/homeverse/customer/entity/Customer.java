package com.homeverse.customer.entity;

import com.homeverse.common.entity.BaseAuditEntity;
import com.homeverse.customer.model.json.LifestyleProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.List;

@Entity
@Table(name = "customers")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Customer extends BaseAuditEntity {

    @Id
    private Long id; // Nhận ID do Identity Service sinh ra

    @Column(unique = true, nullable = false)
    private String email;

    private String fullName;
    private String phone;
    private String avatarUrl;
    private String bannerUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private LifestyleProfile lifestyleProfile;

    // --- Thông tin KYC ---
    private String citizenId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> citizenImages;

    private String kycStatus; // VERIFIED, PENDING, UNVERIFIED, REJECTED

    @PrePersist
    protected void onCreateEntity() {
        if (kycStatus == null) kycStatus = "UNVERIFIED";
    }
}