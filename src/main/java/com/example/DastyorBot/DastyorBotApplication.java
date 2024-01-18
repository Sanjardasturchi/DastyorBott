package com.example.DastyorBot;

import io.github.nazarovctrl.telegrambotspring.annotation.EnableTelegramLongPollingBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableTelegramLongPollingBot
@SpringBootApplication
public class DastyorBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DastyorBotApplication.class, args);
		System.out.println("App start");
		System.out.println("App start");


	}

}
