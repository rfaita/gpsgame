package com.game;

import com.game.model.*;
import com.game.model.action.ActionType;
import com.game.model.type.PlaceType;
import com.game.model.type.SituationType;
import com.game.repository.*;
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

        this.playerRepository.deleteAll()
                .then(this.playerRepository
                        .saveAll(List.of(
                                Player.builder().id("rfaita").build(),
                                Player.builder().id("gh").build()))
                        .collectList()
                )
                .subscribe(player -> log.info("#### Saved players: {}", player));


        this.placeRepository.deleteAll()
                .then(this.placeRepository
                        .saveAll(List.of(
                                Place.builder().name("Small House")
                                        .id("small_house")
                                        .type(PlaceType.COMMON).build(),
                                Place.builder().name("Small Apartment Block")
                                        .id("small_apt_block")
                                        .type(PlaceType.COMMON).build(),
                                Place.builder().name("Police Station")
                                        .id("police_station")
                                        .type(PlaceType.POLICE).build(),
                                Place.builder().name("Hospital")
                                        .id("hospital")
                                        .type(PlaceType.MEDICAL).build()))
                        .collectList()
                )
                .subscribe(place -> log.info("#### Saved place: {}", place));

        this.roomRepository.deleteAll()
                .then(this.roomRepository
                        .saveAll(List.of(
                                Room.builder().id("room")
                                        .usedInPlaces(List.of("small_house", "small_apt_block", "police_station", "hospital")).build(),
                                Room.builder().id("weapon_room")
                                        .usedInPlaces(List.of("police_station")).build(),
                                Room.builder().id("entrance")
                                        .usedInPlaces(List.of("police_station")).build(),
                                Room.builder().id("kitchen")
                                        .usedInPlaces(List.of("small_house", "small_apt_block", "police_station")).build(),
                                Room.builder().id("tv_room")
                                        .usedInPlaces(List.of("small_house", "small_apt_block")).build(),
                                Room.builder().id("balcony")
                                        .usedInPlaces(List.of("small_apt_block")).build()))
                        .collectList()
                )
                .subscribe(room -> log.info("#### Saved rooms: {}", room));


        this.situationRepository.deleteAll()
                .then(this.situationRepository
                        .saveAll(List.of(
                                Situation.builder().type(SituationType.FIRST)
                                        .id("on_fire")
                                        .usedInPlaces(List.of("small_house", "small_apt_block"))
                                        .usedInRooms(List.of("room", "weapon_room", "entrance", "tv_room"))
                                        .usedInSituations(List.of())
                                        .build(),
                                Situation.builder().type(SituationType.FIRST)
                                        .id("car_crashed_at_door")
                                        .usedInPlaces(List.of("police_station"))
                                        .usedInRooms(List.of())
                                        .usedInSituations(List.of())
                                        .build(),
                                Situation.builder().type(SituationType.FIRST)
                                        .id("horde_at_door")
                                        .usedInPlaces(List.of("small_house", "small_apt_block", "police_station"))
                                        .usedInRooms(List.of("weapon_room", "entrance", "balcony"))
                                        .usedInSituations(List.of())
                                        .build(),
                                Situation.builder().type(SituationType.FIRST)
                                        .id("normal")
                                        .usedInPlaces(List.of("small_house", "small_apt_block", "police_station"))
                                        .usedInRooms(List.of("room", "weapon_room", "entrance", "kitchen", "tv_room", "balcony"))
                                        .usedInSituations(List.of())
                                        .build()
                        ))
                        .collectList()
                )
                .subscribe(sit -> log.info("#### Saved situation: {}", sit));


        this.actionRepository.deleteAll()
                .then(this.actionRepository
                        .saveAll(List.of(
                                Action.builder().id("next_room").type(ActionType.NEXT_ROOM)
                                        .usedInSituations(List.of("on_fire", "car_crashed_at_door", "horde_at_door", "normal")).build()
                        ))
                        .collectList()
                )
                .subscribe(action -> log.info("#### Saved actions: {}", action));

    }
}
