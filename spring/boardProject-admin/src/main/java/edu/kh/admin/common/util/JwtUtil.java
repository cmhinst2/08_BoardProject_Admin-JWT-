package edu.kh.admin.common.util;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component // 스프링 빈으로 등록
public class JwtUtil {

	// Secret Key (비밀 키)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 만료 시간 (Access Token: 15분, Refresh Token: 7일)
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15분
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7일

    /**
     * Access Token 생성
     */
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email) // 토큰에 이메일을 설정
                .setIssuedAt(new Date()) // 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY)) // 만료 시간
                .signWith(secretKey) // 서명
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email) // 토큰에 이메일을 설정
                .setIssuedAt(new Date()) // 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY)) // 만료 시간
                .signWith(secretKey) // 서명
                .compact();
    }

    /**
     * 토큰에서 사용자 이메일 추출
     */
    public String extractUserEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // 비밀 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody()
                .getSubject(); // 이메일 (Subject) 반환
    }
    

    /**
     * 토큰 유효성 검증
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey) // 비밀 키 설정
                .build()
                .parseClaimsJws(token); // 토큰 파싱
            return true; // 유효한 토큰
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 유효하지 않은 토큰
        }
    }
}
