package com.studyjun.lottoweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LottowebApplication {

	public static void main(String[] args) {
		SpringApplication.run(LottowebApplication.class, args);
	}

}
