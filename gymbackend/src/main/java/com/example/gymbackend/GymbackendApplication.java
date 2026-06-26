package com.example.gymbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymbackendApplication.class, args);
	}

}
