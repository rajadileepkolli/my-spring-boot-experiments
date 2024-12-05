package com.example.mongoes.config;

import com.example.mongoes.utils.AppConstants;
import com.example.mongoes.web.service.RestaurantService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile(AppConstants.PROFILE_NOT_TEST)
public class Initializer implements CommandLineRunner {

    private final RestaurantService restaurantService;

    public Initializer(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public void run(String... args) throws IOException {
        log.info("Running Initializer.....");
        restaurantService
                .deleteAll()
                .thenMany(restaurantService.loadData())
                .log()
                .subscribe(null, null, () -> log.info("done initialization..."));
    }

    @EventListener(ApplicationStartedEvent.class)
    public void startListeningToChangeStream() {
        log.info("Inside ApplicationStartedEvent");
        this.restaurantService.changeStreamProcessor().log().subscribe();
    }
}
