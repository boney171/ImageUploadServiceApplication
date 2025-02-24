package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableAsync
public class SynchronyApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(SynchronyApplication.class, args);
    }

}
