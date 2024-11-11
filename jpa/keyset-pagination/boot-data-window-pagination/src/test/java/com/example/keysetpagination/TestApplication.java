package com.example.keysetpagination;

import com.example.keysetpagination.common.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .withAdditionalProfiles("local")
                .with(ContainersConfig.class)
                .run(args);
    }
}