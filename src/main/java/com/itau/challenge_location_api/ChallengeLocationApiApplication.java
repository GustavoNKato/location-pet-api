package com.itau.challenge_location_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChallengeLocationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeLocationApiApplication.class, args);
	}

}
