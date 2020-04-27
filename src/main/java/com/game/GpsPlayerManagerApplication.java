package com.game;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class GpsPlayerManagerApplication{

    public static void main(String[] args) {
        SpringApplication.run(GpsPlayerManagerApplication.class, args);
        Hooks.onOperatorDebug();
    }

}
