package edu.kh.admin.common.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import edu.kh.admin.auth.model.service.AuthService;

@Configuration
@EnableScheduling
public class RefreshTokenScheduler {

	@Autowired
	private AuthService service;

	//@Scheduled(cron = "0,30 * * * * *") // test용
	@Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
	public void scheduleTokenCleanup() {
		service.cleanupExpiredTokens();
	}
}
