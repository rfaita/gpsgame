package com.game.gps.player.manager;

import com.game.gps.player.manager.model.Place;
import com.game.gps.player.manager.model.Situation;
import com.game.gps.player.manager.model.type.PlaceType;
import com.game.gps.player.manager.model.type.SituationType;
import com.game.gps.player.manager.repository.PlaceRepository;
import com.game.gps.player.manager.repository.SituationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Hooks;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class GpsPlayerManagerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GpsPlayerManagerApplication.class, args);
        Hooks.onOperatorDebug();
    }

    private final PlaceRepository placeRepository;
    private final SituationRepository situationRepository;

    @Override
    public void run(String... args) throws Exception {

        this.placeRepository.saveAll(List.of(
                Place.builder().name("Small House")
                        .id("1")
                        .description("You see a small house.")
                        .type(PlaceType.COMMON).build(),
                Place.builder().name("Small Apartment Block")
                        .id("2")
                        .description("You see a small apartment block, probably has between four and six small apartments.")
                        .type(PlaceType.COMMON).build(),
                Place.builder().name("Police Station")
                        .id("3")
                        .description("You see a police station, this can be a dangerous place.")
                        .type(PlaceType.POLICE).build()
        )).subscribe(place -> log.info("Saved place: {}", place));


        this.situationRepository.saveAll(List.of(
                Situation.builder().type(SituationType.FIRST)
                        .id("1")
                        .usedInPlaces(List.of("1", "2"))
                        .description("teste 1")
                        .build(),
                Situation.builder().type(SituationType.FIRST)
                        .id("2")
                        .usedInPlaces(List.of("2", "3"))
                        .description("teste 2")
                        .build(),
                Situation.builder().type(SituationType.FIRST)
                        .id("3")
                        .usedInPlaces(List.of("3", "2"))
                        .description("teste 3")
                        .build(),
                Situation.builder().type(SituationType.FIRST)
                        .id("4")
                        .usedInPlaces(List.of("1", "3"))
                        .description("teste 4")
                        .build()
        )).subscribe(sit -> log.info("Saved situation: {}", sit));


    }
}
