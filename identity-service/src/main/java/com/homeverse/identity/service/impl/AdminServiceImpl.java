package com.homeverse.identity.service.impl;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID: " + userId));
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        UserCredential user = findUserById(userId);

        // Cấm tự khóa tài khoản của chính mình (nếu cần bảo vệ)
        if (user.getRole() == UserCredential.Role.ADMIN) {
            throw new RuntimeException("Không thể khóa tài khoản của Admin khác!");
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
        // để các service khác (như customer-service) cũng xóa dữ liệu liên quan đi.
    }

    @Override
    @Transactional
    public void promoteToAdmin(Long userId) {
        UserCredential user = findUserById(userId);
        user.setRole(UserCredential.Role.ADMIN);
        userRepository.save(user);
    }
}