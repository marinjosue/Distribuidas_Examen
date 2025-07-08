package com.eval.conjunta.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnvironmentalAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvironmentalAnalyzerApplication.class, args);
	}

}
