package com.eval.conjunta.sensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableTransactionManagement
public class SensorDataCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorDataCollectorApplication.class, args);
	}
}
