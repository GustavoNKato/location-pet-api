package com.itau.challenge_localization_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChallengeLocalizationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeLocalizationApiApplication.class, args);
	}

}
