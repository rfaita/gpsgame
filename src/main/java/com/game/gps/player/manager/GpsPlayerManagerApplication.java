package com.game.gps.player.manager;

import com.game.gps.player.manager.model.*;
import com.game.gps.player.manager.model.action.ActionType;
import com.game.gps.player.manager.model.type.PlaceType;
import com.game.gps.player.manager.model.type.SituationType;
import com.game.gps.player.manager.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
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

    private final PlayerRepository playerRepository;
    private final PlaceRepository placeRepository;
    private final SituationRepository situationRepository;
    private final RoomRepository roomRepository;
    private final ActionRepository actionRepository;

    @Override
    public void run(String... args) throws Exception {

        this.placeRepository.saveAll(List.of(
                Place.builder().name("Small House")
                        .id("1")
                        .type(PlaceType.COMMON).build(),
                Place.builder().name("Small Apartment Block")
                        .id("2")
                        .type(PlaceType.COMMON).build(),
                Place.builder().name("Police Station")
                        .id("3")
                        .type(PlaceType.POLICE).build()
        )).subscribe(place -> log.info("Saved place: {}", place));


        this.situationRepository.saveAll(List.of(
                Situation.builder().type(SituationType.FIRST)
                        .id("1")
                        .usedInPlaces(List.of("1", "2"))
                        .usedInRooms(List.of("1"))
                        .usedInSituations(List.of("2"))
                        .build(),
                Situation.builder().type(SituationType.FIRST)
                        .id("2")
                        .usedInPlaces(List.of("2", "3"))
                        .usedInRooms(List.of("1"))
                        .usedInSituations(List.of("3"))
                        .build(),
                Situation.builder().type(SituationType.FIRST)
                        .id("3")
                        .usedInPlaces(List.of("3", "2"))
                        .usedInRooms(List.of("2"))
                        .usedInSituations(List.of("4"))
                        .build(),
                Situation.builder().type(SituationType.FIRST)
                        .id("4")
                        .usedInPlaces(List.of("1", "3"))
                        .usedInRooms(List.of("3"))
                        .usedInSituations(List.of("1"))
                        .build()
        )).subscribe(sit -> log.info("Saved situation: {}", sit));

        this.playerRepository.saveAll(List.of(
                Player.builder().id("rfaita").build(),
                Player.builder().id("gh").build()
        )).subscribe(player -> log.info("Saved players: {}", player));

        this.roomRepository.saveAll(List.of(
                Room.builder().id("1").name("room1").usedInPlaces(List.of("1")).build(),
                Room.builder().id("2").name("room2").usedInPlaces(List.of("2")).build(),
                Room.builder().id("3").name("room3").usedInPlaces(List.of("3")).build()
        )).subscribe(room -> log.info("Saved rooms: {}", room));

        this.actionRepository.saveAll(List.of(
                Action.builder().id("1").type(ActionType.NEXT_DOOR).usedInSituations(List.of("1")).build(),
                Action.builder().id("2").type(ActionType.NEXT_DOOR).usedInSituations(List.of("2")).build(),
                Action.builder().id("3").type(ActionType.NEXT_DOOR).usedInSituations(List.of("3")).build(),
                Action.builder().id("4").type(ActionType.NEXT_DOOR).usedInSituations(List.of("4")).build()
        )).subscribe(action -> log.info("Saved actions: {}", action));

    }
}
