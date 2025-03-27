package com.example.demo;


import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.example")
public class DemoApplication {

	public static void main(String[] args) {
		if (System.getProperty("DB_URL") == null) {
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			System.setProperty("DB_URL", dotenv.get("DB_URL", "jdbc:postgresql://localhost:5432/postgres"));
			System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME", "postgres"));
			System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD", "defaultPassword"));
			System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST", "smtp.gmail.com"));
			System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT", "587"));
			System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME", "yourLocalMail"));
			System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD", "yourLocalMailPassword"));
		}
		SpringApplication.run(DemoApplication.class, args);
	

	}
}
