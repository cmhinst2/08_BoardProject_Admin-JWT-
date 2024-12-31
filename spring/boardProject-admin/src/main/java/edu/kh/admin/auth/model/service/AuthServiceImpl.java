package edu.kh.admin.auth.model.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.admin.auth.model.mapper.RefreshMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

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

	// 만료된 리프레시 토큰 삭제
	@Override
	public void cleanupExpiredTokens() {
		try {
			LocalDateTime now = LocalDateTime.now();

			// 먼저 만료된 토큰 수를 확인
			int expiredCount = refreshMapper.countExpiredTokens(now);
			log.info("만료된 토큰 갯수 {} 개 찾음", expiredCount);

			if (expiredCount > 0) {
				// 실제 삭제 수행

				int deletedCount = refreshMapper.deleteExpiredTokens(now);
				log.info("성공 : 만료 토큰 {}개 삭제", deletedCount);

				// 삭제 결과 검증
				if (deletedCount != expiredCount) {
					log.warn("만료된 토큰 갯수({}) 와 삭제된 갯수({}) 불일치", 
							expiredCount, deletedCount);
				}
			}
		} catch (Exception e) {
			log.error("예외 : 만료된 리프레시 토큰 삭제 중 발생", e);
			throw new RuntimeException("만료 토큰 삭제 중 예외 발생", e);
		}
	}
}
