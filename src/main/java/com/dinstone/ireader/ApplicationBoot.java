package com.dinstone.ireader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApplicationBoot {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ApplicationBoot.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Bean
    Configuration configuration() {
        return new Configuration();
    }
}
