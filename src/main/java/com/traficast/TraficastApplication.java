package com.traficast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TraficastApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraficastApplication.class, args);
	}

}
