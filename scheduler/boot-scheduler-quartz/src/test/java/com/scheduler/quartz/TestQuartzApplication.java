package com.scheduler.quartz;

import com.scheduler.quartz.common.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestQuartzApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.from(QuartzApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}