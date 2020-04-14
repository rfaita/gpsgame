package com.game.gps.player.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GpsPlayerManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GpsPlayerManagerApplication.class, args);
    }

}
