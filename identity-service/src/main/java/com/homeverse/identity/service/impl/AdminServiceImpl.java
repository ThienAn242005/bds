package com.homeverse.identity.service.impl;

import com.homeverse.common.exception.AppException; // SỬA Ở ĐÂY
import com.homeverse.common.exception.ErrorCode;   // SỬA Ở ĐÂY
import com.homeverse.identity.entity.UserCredential;
import com.homeverse.identity.repository.UserCredentialRepository;
import com.homeverse.identity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserCredentialRepository userRepository;

    private UserCredential findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        UserCredential user = findUserById(userId);

        // Cấm tự khóa tài khoản của chính mình (nếu cần bảo vệ)
        if (user.getRole() == UserCredential.Role.ADMIN) {
            // SỬA Ở ĐÂY: Có thể dùng UNAUTHORIZED hoặc thêm mã lỗi mới
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Đảo ngược trạng thái: Đang true thành false, đang false thành true
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserCredential user = findUserById(userId);
        userRepository.delete(user);

        // TODO: Gửi message qua Kafka "USER_DELETED_EVENT"
    }

    @Override
    @Transactional
    public void promoteToAdmin(Long userId) {
        UserCredential user = findUserById(userId);
        user.setRole(UserCredential.Role.ADMIN);
        userRepository.save(user);
    }
}