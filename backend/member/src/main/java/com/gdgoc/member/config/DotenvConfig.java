package com.gdgoc.member.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();

            // .env 파일의 변수들을 시스템 환경 변수로 설정
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            });
        } catch (Exception e) {
            System.out.println("Warning: .env file not found or could not be loaded: " + e.getMessage());
        }
    }
}

