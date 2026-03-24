package com.homeverse.identity.service.impl;

import com.homeverse.common.exception.AppException; // SỬA Ở ĐÂY: Import AppException
import com.homeverse.common.exception.ErrorCode;
import org.springframework.security.authentication.DisabledException;
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
import org.springframework.security.authentication.BadCredentialsException; // SỬA Ở ĐÂY
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
            // SỬA Ở ĐÂY: Dùng AppException + ErrorCode
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        UserCredential user = userMapper.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(UserCredential.Role.USER);

        UserCredential savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginDTO loginDTO) {
        try {
            // 1. Spring Security tự động lo việc check Email và Password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        } catch (DisabledException e) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        // 2. Lấy thông tin User ra
        UserCredential user = userRepository.findByEmail(loginDTO.getEmail())
                // SỬA Ở ĐÂY: Dùng AppException + ErrorCode
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isActive()) {
            // SỬA Ở ĐÂY: Dùng AppException + ErrorCode
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 3. Đóng gói thêm userId
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("userId", user.getId());

        // 4. Sinh Token có chứa userId
        String token = jwtUtils.generateToken(extraClaims, user);
        // =======================

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
                // SỬA Ở ĐÂY: Dùng AppException + ErrorCode
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 1. Tạo một mã token ngẫu nhiên (UUID)
        String token = UUID.randomUUID().toString();

        // 2. Lưu token vào Database với thời hạn 15 phút
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);

        // 3. Giả lập gửi Email
        System.out.println("\n========================================================");
        System.out.println("HỆ THỐNG ĐANG GỬI EMAIL ĐẾN: " + email);
        System.out.println("Nội dung: Vui lòng sử dụng mã token sau để đặt lại mật khẩu:");
        System.out.println("MÃ TOKEN TEST: " + token);
        System.out.println("========================================================\n");
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // 1. Tìm token trong Database
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                // SỬA Ở ĐÂY: Thêm 1 ErrorCode mới hoặc dùng INVALID_REQUEST tạm
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST)); // Tốt nhất nên thêm ErrorCode.INVALID_TOKEN

        // 2. Kiểm tra hạn sử dụng
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken); // Xóa rác
            // SỬA Ở ĐÂY
            throw new AppException(ErrorCode.INVALID_REQUEST); // Tốt nhất nên thêm ErrorCode.TOKEN_EXPIRED
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
                // SỬA Ở ĐÂY
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            // SỬA Ở ĐÂY
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        // Lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}