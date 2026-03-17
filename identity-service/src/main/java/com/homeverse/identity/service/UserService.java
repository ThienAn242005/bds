package com.homeverse.identity.service;

import com.homeverse.identity.dto.request.*;
import com.homeverse.identity.dto.response.*;

import java.util.List;

public interface UserService {
    UserResponseDTO register(UserRegisterDTO registerDTO);
    AuthResponse login(LoginDTO loginDTO);
    void upgradeToLandlord();
    void submitKyc(KycRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    void toggleUserStatus(Long id);
    void approveKYC(Long userId, ApproveRequestDTO dto);
    void deleteUser(Long id);
}