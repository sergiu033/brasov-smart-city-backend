package com.smartcity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartCityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartCityAppApplication.class, args);
	}

}
