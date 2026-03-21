package com.homeverse.identity.client;

import com.homeverse.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping; // <-- CHÚ Ý IMPORT NÀY
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "${homeverse.services.customer}")
public interface CustomerServiceClient {


    @PostMapping("/customers/init")
    ApiResponse<String> initProfile(@RequestBody CustomerInitRequest request);

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    class CustomerInitRequest {
        private Long id;
        private String email;
        private String fullName;
        private String phone;
    }
}