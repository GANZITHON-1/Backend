package com.likelion.ganzithon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GanzithonApplication {

    public static void main(String[] args) {
        SpringApplication.run(GanzithonApplication.class, args);
    }

}
