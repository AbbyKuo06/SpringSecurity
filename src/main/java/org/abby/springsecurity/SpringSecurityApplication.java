package org.abby.springsecurity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
		log.info("{}", "========== @Slf4j info log 生效 ==========");
		log.debug("{}", "========== @Slf4j debug log 生效 ==========");
	}

}
