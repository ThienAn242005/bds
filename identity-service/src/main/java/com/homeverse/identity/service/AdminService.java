package com.homeverse.identity.service;

public interface AdminService {
    void toggleUserStatus(Long userId);
    void deleteUser(Long userId);
    void promoteToAdmin(Long userId);
}