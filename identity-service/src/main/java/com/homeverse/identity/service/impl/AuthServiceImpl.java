package com.homeverse.identity.service.impl;

import com.homeverse.identity.dto.request.*;
import com.homeverse.identity.dto.response.*;
import com.homeverse.identity.entity.PasswordResetToken;
import com.homeverse.identity.entity.UserCredential;
import com.homeverse.identity.mapper.UserMapper;
import com.homeverse.identity.repository.PasswordResetTokenRepository;
import com.homeverse.identity.repository.UserCredentialRepository;
import com.homeverse.identity.service.AuthService;
import com.homeverse.identity.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserCredentialRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserResponseDTO register(UserRegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        UserCredential user = userMapper.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));


        user.setRole(UserCredential.Role.USER);

        UserCredential savedUser = userRepository.save(user);

        // TODO: Gọi API / Bắn Kafka báo cho customer-service khởi tạo Profile

        return userMapper.toResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        UserCredential user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        }

        String token = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
    @Override
    @Transactional
    public void sendForgotPasswordEmail(String email) {
        UserCredential user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này!"));

        // 1. Tạo một mã token ngẫu nhiên (UUID)
        String token = UUID.randomUUID().toString();

        // 2. Lưu token vào Database với thời hạn 15 phút
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);

        // 3. Giả lập gửi Email (In ra Console để bạn dễ dàng test bằng Postman)
        System.out.println("\n========================================================");
        System.out.println("HỆ THỐNG ĐANG GỬI EMAIL ĐẾN: " + email);
        System.out.println("Nội dung: Vui lòng sử dụng mã token sau để đặt lại mật khẩu:");
        System.out.println("MÃ TOKEN TEST: " + token);
        System.out.println("========================================================\n");

        // TODO: Sau này tích hợp JavaMailSender thật vào đây
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // 1. Tìm token trong Database
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Mã xác nhận không hợp lệ hoặc không tồn tại!"));

        // 2. Kiểm tra hạn sử dụng
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken); // Xóa rác
            throw new RuntimeException("Mã xác nhận đã hết hạn! Vui lòng yêu cầu gửi lại email.");
        }

        // 3. Cập nhật mật khẩu mới cho User
        UserCredential user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 4. Xóa token đi để không bị dùng lại lần 2
        tokenRepository.delete(resetToken);
    }
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // Lấy email của người đang đăng nhập từ Security Context (lấy từ JWT)
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserCredential user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }

        // Lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}