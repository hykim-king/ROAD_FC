package com.pcwk.ehr;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pcwk.ehr.member.MemberService;

@SpringBootTest
class UserJUnit {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	MemberService userService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@BeforeEach
	void setUp() throws Exception {
		log.info("--------------------------------");
		log.info("setUp()");
		log.info("--------------------------------");
	}

	@AfterEach
	void tearDown() throws Exception {
		log.info("--------------------------------");
		log.info("tearDown()");
		log.info("--------------------------------");
		
	}
	
	@Test
	void doSave() {
		log.info("--------------------------------");
		log.info("doSave()");
		log.info("--------------------------------");
		
		for(int i=0; i<=100; i++) {
			userService.create("unknown"+i, "1234",i+"testing@test.com", "무명"+i);			
		}
	}
	
	@Disabled
	@Test
	void bean() {
		log.info("--------------------------------");
		log.info("bean()");
		log.info("--------------------------------");

		log.info("userService:{}",userService);
		
		assertNotNull(userService);
		assertNotNull(passwordEncoder);
	}

}
