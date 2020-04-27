package com.game.runner;

import com.game.model.*;
import com.game.model.action.ActionType;
import com.game.model.type.PlaceType;
import com.game.model.type.SituationType;
import com.game.repository.*;
import com.game.util.CsvUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class DatabaseLoaderRunner implements CommandLineRunner {

    private final PlayerRepository playerRepository;
    private final PlaceRepository placeRepository;
    private final SituationRepository situationRepository;
    private final RoomRepository roomRepository;
    private final ActionRepository actionRepository;

    private List<String> listOfObject(Object o) {
        if (o != null) {
            return List.of(((String) o).split(","));
        } else {
            return List.of();
        }
    }

    private void savePlacesFromCsv(List<List<String>> data) {
        this.placeRepository.deleteAll()
                .then(
                        Flux.just(data.stream()
                                .filter(line -> "PLACE".equalsIgnoreCase(line.get(0)))
                                .collect(Collectors.toList())
                                .toArray())
                                .cast(List.class)
                                .map(field -> Place.builder()
                                        .id((String) field.get(1))
                                        .type(PlaceType.valueOf((String) field.get(2)))
                                        .build())
                                .collectList()
                                .flatMap(obj -> this.placeRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved places: {}", place));
    }

    private void saveRoomsFromCsv(List<List<String>> data) {
        this.roomRepository.deleteAll()
                .then(
                        Flux.just(data.stream()
                                .filter(line -> "ROOM".equalsIgnoreCase(line.get(0)))
                                .collect(Collectors.toList())
                                .toArray())
                                .cast(List.class)
                                .map(field -> Room.builder()
                                        .id((String) field.get(1))
                                        .usedInPlaces(listOfObject(field.get(2)))
                                        .build())
                                .collectList()
                                .flatMap(obj -> this.roomRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved rooms: {}", place));
    }

    private void saveSituationsFromCsv(List<List<String>> data) {
        this.situationRepository.deleteAll()
                .then(
                        Flux.just(data.stream()
                                .filter(line -> "SITUATION".equalsIgnoreCase(line.get(0)))
                                .collect(Collectors.toList())
                                .toArray())
                                .cast(List.class)
                                .map(field -> Situation.builder()
                                        .id((String) field.get(1))
                                        .type(SituationType.valueOf((String) field.get(2)))
                                        .maxSurvivors(Integer.valueOf((String) field.get(3)))
                                        .maxZombies(Integer.valueOf((String) field.get(4)))
                                        .usedInPlaces(listOfObject(field.get(5)))
                                        .usedInRooms(listOfObject(field.get(6)))
                                        .usedInSituations(listOfObject(field.get(7)))
                                        .build())
                                .collectList()
                                .flatMap(obj -> this.situationRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved situations: {}", place));
    }


    private void saveActionsFromCsv(List<List<String>> data) {
        this.actionRepository.deleteAll()
                .then(
                        Flux.just(data.stream()
                                .filter(line -> "ACTION".equalsIgnoreCase(line.get(0)))
                                .collect(Collectors.toList())
                                .toArray())
                                .cast(List.class)
                                .map(field -> Action.builder()
                                        .id((String) field.get(1))
                                        .type(ActionType.valueOf((String) field.get(2)))
                                        .usedInSituations(listOfObject(field.get(3)))
                                        .build())
                                .collectList()
                                .flatMap(obj -> this.actionRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved actions: {}", place));
    }

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


        log.info("#### Loading database.....");

        File file = ResourceUtils.getFile("classpath:database/sample.csv");

        List<List<String>> data = CsvUtil.getCsvFile(file);

        this.savePlacesFromCsv(data);
        this.saveRoomsFromCsv(data);
        this.saveSituationsFromCsv(data);
        this.saveActionsFromCsv(data);


    }


}
