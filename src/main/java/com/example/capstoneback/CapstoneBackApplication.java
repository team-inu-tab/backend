package com.example.capstoneback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CapstoneBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapstoneBackApplication.class, args);
	}

}
