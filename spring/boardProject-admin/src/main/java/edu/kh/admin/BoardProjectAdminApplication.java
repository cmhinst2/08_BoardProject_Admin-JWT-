package edu.kh.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class BoardProjectAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectAdminApplication.class, args);
	}

}
