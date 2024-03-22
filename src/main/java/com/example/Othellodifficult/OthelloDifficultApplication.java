package com.example.Othellodifficult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.OffsetDateTime;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.Othellodifficult"})
public class OthelloDifficultApplication {

	public static void main(String[] args) {
		SpringApplication.run(OthelloDifficultApplication.class, args);
	}
}
