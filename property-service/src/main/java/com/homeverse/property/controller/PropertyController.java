package com.homeverse.property.controller;

import com.homeverse.common.dto.ApiResponse;
import com.homeverse.property.dto.PropertyRequestDTO;
import com.homeverse.property.dto.PropertyResponseDTO;
import com.homeverse.property.service.PropertyService;
// Giả định bạn có JwtUtils ở property-service để giải mã token
// import com.homeverse.property.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    // private final JwtUtils jwtUtils;

    @PostMapping
    public ApiResponse<PropertyResponseDTO> createProperty(
            @RequestBody PropertyRequestDTO request,
            @RequestHeader("Authorization") String token) {

        // Tạm thời hardcode landlordId = 1 để test.
        // Sau khi bạn copy JwtUtils sang, hãy dùng: Long landlordId = jwtUtils.extractUserId(token.substring(7));
        Long landlordId = 1L;

        PropertyResponseDTO response = propertyService.createProperty(request, landlordId);
        return ApiResponse.<PropertyResponseDTO>builder()
                .code(200)
                .message("Tạo phòng trọ thành công")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PropertyResponseDTO> updateProperty(
            @PathVariable Long id,
            @RequestBody PropertyRequestDTO request,
            @RequestHeader("Authorization") String token) {

        Long landlordId = 1L; // Thay bằng jwtUtils sau
        PropertyResponseDTO response = propertyService.updateProperty(id, request, landlordId);

        return ApiResponse.<PropertyResponseDTO>builder()
                .code(200)
                .message("Cập nhật phòng trọ thành công")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProperty(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long landlordId = 1L; // Thay bằng jwtUtils sau
        propertyService.deleteProperty(id, landlordId);

        return ApiResponse.<String>builder()
                .code(200)
                .message("Xóa phòng trọ thành công")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PropertyResponseDTO> getPropertyById(@PathVariable Long id) {
        PropertyResponseDTO response = propertyService.getPropertyById(id);
        return ApiResponse.<PropertyResponseDTO>builder()
                .code(200)
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<PropertyResponseDTO>> getAllProperties() {
        List<PropertyResponseDTO> response = propertyService.getAllProperties();
        return ApiResponse.<List<PropertyResponseDTO>>builder()
                .code(200)
                .result(response)
                .build();
    }
}