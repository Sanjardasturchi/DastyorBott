package com.example.DastyorBot;

import com.dark.programs.speech.translator.GoogleTranslate;
import com.example.DastyorBot.entity.ProfileEntity;
import com.example.DastyorBot.repository.ProfileRepository;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.github.nazarovctrl.telegrambotspring.annotation.EnableTelegramLongPollingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@EnableTelegramLongPollingBot
@SpringBootApplication
public class DastyorBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DastyorBotApplication.class, args);
        System.out.println("App start");
        System.out.println("App start");

//        try {
//            System.out.println(GoogleTranslate.translate("uz", "en", "Salom"));
//            //English to IGBO
//            System.out.println(GoogleTranslate.translate("uz", "how are you"));
//
//            //English to GREEK
//            System.out.println(GoogleTranslate.translate("ru", "how are you"));
//
//            //English to HAUSA
//            System.out.println(GoogleTranslate.translate("ha", "how are you"));
//
//            //English to Yoruba
//            System.out.println(GoogleTranslate.translate("yo", "how are you"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
