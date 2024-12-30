package edu.kh.admin.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.admin.common.interceptor.JwtInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer{

	   @Autowired
	    private JwtInterceptor jwtInterceptor;

	    @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        registry.addInterceptor(jwtInterceptor)
	                .addPathPatterns("/**")  // 모든 요청에 대해 인터셉터 적용
	                .excludePathPatterns("/auth/login", "/auth/refresh", "/auth/logout"); // 로그인, 리프레시 요청은 제외
	    }
}
