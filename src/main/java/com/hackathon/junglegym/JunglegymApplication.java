package com.hackathon.junglegym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JunglegymApplication {

  public static void main(String[] args) {
    SpringApplication.run(JunglegymApplication.class, args);
  }
}
