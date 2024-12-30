package edu.kh.admin.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import edu.kh.admin.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
	/*
     * 인증이 필요한 경우는 사용자가 시스템이나 애플리케이션의 특정 리소스에 접근하거나 작업을 수행하려고 할 때, 사용자의 신원을 확인하고 이를 통해 해당 리소스에 접근할 수 있는 권한이 있는지를 판단해야 하는 상황을 의미
     * ex) 개인 정보 조회, 상세 정보 요청, 유저 관리, 시스템 설정 등 - 관리자 페이지의 거의 모든것
     * == 거의 모든 요청 헤더에 accessToken이 있는지 확인해야한다!
     * */

	@Autowired
    private JwtUtil jwtUtil;

    // 요청이 컨트롤러로 전달되기 전에 호출
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	
    	// OPTIONS 요청은 CORS 프리플라이트 요청이므로, 별도로 인증을 체크하지 않고 허용
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        
        String accessToken = request.getHeader("Authorization");

        // Authorization 헤더에서 'Bearer '를 제외한 토큰 추출
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7); // "Bearer " 제외한 부분
            log.debug("인터셉터 accessToken {}", accessToken);

            // 토큰 유효성 검사
            if (!jwtUtil.isTokenValid(accessToken)) {
            	//System.out.println("accessToken 유효하지 않음. 만료시간끝!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
                response.getWriter().write("Invalid Access Token");
                return false;  // 요청을 더 이상 처리하지 않음
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.getWriter().write("Access Token is missing");
            return false;  // 요청을 더 이상 처리하지 않음
        }
        
        return true; // 유효한 토큰인 경우, 요청을 계속 처리
    }
}

