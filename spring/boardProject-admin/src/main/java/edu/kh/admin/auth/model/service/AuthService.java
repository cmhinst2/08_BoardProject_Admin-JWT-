package edu.kh.admin.auth.model.service;

import java.util.Date;

public interface AuthService {

	// Refresh Token 저장
	int saveRefreshToken(int memberNo, String memberEmail, String refreshToken, Date expirationDate);

	// Refresh Token 일치 여부 조회
	String matchRefreshToken(String refreshToken);

	// 이메일 일치하는 Refresh Token 삭제
	void deleteRefreshToken(String memberEmail);
}
