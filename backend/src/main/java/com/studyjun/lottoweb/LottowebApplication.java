package com.studyjun.lottoweb;

import com.studyjun.lottoweb.config.ScheduledJobs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LottowebApplication {
	private final ScheduledJobs scheduledJobs;

	public LottowebApplication(ScheduledJobs scheduledJobs) {
		this.scheduledJobs = scheduledJobs;
	}

	public static void main(String[] args) {
		SpringApplication.run(LottowebApplication.class, args);
	}

	@Bean
	public CommandLineRunner testScheduler() {
		return args -> {
			System.out.println("Executing scheduled method manually...");
			scheduledJobs.updateLottoArff();
		};
	}
}