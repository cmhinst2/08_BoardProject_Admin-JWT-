package edu.kh.admin.auth.model.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.admin.auth.model.mapper.RefreshMapper;
import lombok.RequiredArgsConstructor;

@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private final RefreshMapper refreshMapper;
	
	// Refresh Token 저장
	@Override
	public int saveRefreshToken(int memberNo, String memberEmail, String refreshToken, Date expirationDate) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", memberNo);
		map.put("memberEmail", memberEmail);
		map.put("refreshToken", refreshToken);
		map.put("expirationDate", expirationDate);
		
		return refreshMapper.insertRefreshToken(map);
		
	}

	
	// Refresh Token 일치 여부 조회
	@Override
	public String matchRefreshToken(String refreshToken) {
		return refreshMapper.matchRefreshToken(refreshToken);
	}
	
	// 이메일 일치하는 Refresh Token 삭제
	@Override
	public void deleteRefreshToken(String memberEmail) {
		refreshMapper.deleteRefreshToken(memberEmail);
	}
	
}
