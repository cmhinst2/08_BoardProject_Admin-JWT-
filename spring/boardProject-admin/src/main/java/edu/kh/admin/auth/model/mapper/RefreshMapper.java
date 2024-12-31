package edu.kh.admin.auth.model.mapper;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshMapper {

	// Refresh Token 삽입  
    int insertRefreshToken(Map<String, Object> map);
    
    // Refresh Token 일치 여부 조회
 	String matchRefreshToken(String refreshToken);

    // 특정 사용자의 Refresh Token 삭제
 	void deleteRefreshToken(String memberEmail);
    
 	// 만료된 토큰 삭제
 	int deleteExpiredTokens(LocalDateTime now);
 	
 	 // 만료된 토큰 수를 미리 확인
    int countExpiredTokens(LocalDateTime now);
 	
    // Refresh Token 조회
    //String findRefreshTokenByUserNo(int memberNo);
    
    // Refresh Token 만료일 조회
    //Date findExpirationDateByUserNo(int memberNo);



 

}
