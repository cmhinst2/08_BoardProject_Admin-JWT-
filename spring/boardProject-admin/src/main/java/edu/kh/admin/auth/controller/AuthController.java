package edu.kh.admin.auth.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.admin.auth.model.dto.AuthResponse;
import edu.kh.admin.auth.model.service.AuthService;
import edu.kh.admin.common.util.JwtUtil;
import edu.kh.admin.main.model.dto.Member;
import edu.kh.admin.main.model.service.AdminService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final JwtUtil jwtUtil;  // JWT 생성 및 검증 유틸리티 클래스
    private final AdminService adminService;
    private final AuthService authService;
    
    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody Member inputMember, HttpServletResponse response) {
      
        //  1. 유저 검증: 이메일 / 비밀번호와 일치하는 유저가 DB에 존재하는지 확인.
    	Member loginMember = adminService.login(inputMember);
    	
		if (loginMember == null) {
			// 이메일이 없거나 비밀번호가 맞지 않으면 401 Unauthorized 응답
			return ResponseEntity.status(401).body("잘못된 증명(유저 정보 없음)");
		}

        // 2. 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(inputMember.getMemberEmail());
        String refreshToken = jwtUtil.generateRefreshToken(inputMember.getMemberEmail());
        
        //log.debug("accessToken : {}", accessToken);
       // log.debug("refreshToken : {}", refreshToken);

        // 3. Refresh Token 저장 - DB에 저장
        // 현재 시간 기준으로 7일 뒤의 만료 날짜 계산
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);  // 7일 후로 설정
        Date expirationDate = calendar.getTime();
        
        int result = authService.saveRefreshToken(loginMember.getMemberNo(), loginMember.getMemberEmail(), refreshToken, expirationDate);

        // 4. 리프레시토큰 httpOnly 쿠키에 저장
        if(result > 0) {
        	// Refresh Token 쿠키 설정
        	Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        	refreshTokenCookie.setHttpOnly(true);
        	refreshTokenCookie.setPath("/");
        	refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        	response.addCookie(refreshTokenCookie);
        	
        	// Access Token과 Refresh Token을 클라이언트로 반환
        	return ResponseEntity.ok(new AuthResponse(accessToken, loginMember));
        	
        } else {
        	return ResponseEntity.status(500).body("리프레시 토큰 저장 중 예외 발생");
        }
    }


    @PostMapping("logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {


        // 클라이언트로부터 전달된 Refresh Token을 추출 (쿠키에서)
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        //System.out.println("refreshToken : " + refreshToken);
        
        // --> refreshToken이 null 이어도 무조건 로그아웃 실행.
        
        // refreshToken이 있는 경우
    	// DB에서 토큰 삭제 시도 -> 쿠키 삭제 -> "로그아웃 성공" 응답 반환.
        
        // refreshToken이 없는 경우 (null)
        // DB 삭제 단계는 생략 -> 쿠키 삭제 -> "로그아웃 성공" 응답 반환.
       
        // 토큰 삭제 중 예외 발생 경우
        // 로그로 기록 -> 쿠키 삭제 -> "로그아웃 성공" 응답 반환.
        
        // 2. refreshToken이 존재하면 DB에서 삭제 시도
        if (refreshToken != null) {
        	try {
                String memberEmail = jwtUtil.extractUserEmail(refreshToken); // 토큰에서 이메일 추출
                if (memberEmail != null) {
                    authService.deleteRefreshToken(memberEmail); // 토큰 삭제 시도
                }
            } catch (Exception e) {
                // 로그에 예외 기록, 하지만 프로세스는 중단하지 않음
                log.error("로그아웃 중 Refresh Token 삭제 오류: ", e);
            }
        	
        }

        // 3. 쿠키에서 Refresh Token 삭제
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setMaxAge(0); // 즉시 만료
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);
       

        // 4. 성공 응답 반환
        return ResponseEntity.ok("로그아웃 성공");
    }
    
    // 리프레시 토큰을 이용해 액세스 토큰 재발급
    @PostMapping("refresh")
    public ResponseEntity<Object> refreshAccessToken(HttpServletRequest request) {
    	   
    	// 요청에서 refresh token을 쿠키로 받아오기
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        

        if (refreshToken == null) {
            return ResponseEntity.status(400).body("Refresh Token이 없습니다.");
        }

        // Refresh Token을 검증
        if (!jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }
        
        try {
        	
        	// 리프레시 토큰을 DB에 가져가서 비교 
        	String memberEmail = authService.matchRefreshToken(refreshToken);
        	
        	if(memberEmail == null) {
        		return ResponseEntity.status(500).body("일치하는 리프레시 토큰 없음");
        	} 
        	
        	// 일치하는 토큰이 있다면 매핑되는 memberEmail을 조회하여 가지고옴.
        	// memberEmail로 새로운 accessToken 발급
        	String newAccessToken = jwtUtil.generateAccessToken(memberEmail);
        			
        	// 새로운 Access Token을 클라이언트에게 반환
        	return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
          
        } catch (Exception e) {
            return ResponseEntity.status(500).body("토큰 갱신 처리 중 오류가 발생했습니다.");
        }
    }


    
}
