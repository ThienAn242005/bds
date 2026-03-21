package com.homeverse.customer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    // BẮT BUỘC PHẢI GIỐNG HỆT SECRET KEY BÊN IDENTITY-SERVICE
    @Value("${jwt.secret:DayLaMotDoanBaoMatRatDaiChoHeThongHomeVerseCuaBan2026DeDungChoThuatToanHS256}")
    private String secretKey;

    // 1. Trích xuất Email (Subject)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Trích xuất Role để set quyền cho SecurityContext
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 3. Kiểm tra Token cơ bản (Dành cho Resource Server)
    // Không cần check UserDetails trong DB, chỉ cần verify chữ ký và hạn sử dụng
    public boolean isTokenValidBasic(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("Lỗi JWT: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}