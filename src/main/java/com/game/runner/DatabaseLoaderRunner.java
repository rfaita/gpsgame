package com.game.runner;

import com.game.model.*;
import com.game.model.action.ActionType;
import com.game.model.minigame.MiniGameDifficult;
import com.game.model.type.PlaceType;
import com.game.model.type.SituationType;
import com.game.repository.*;
import com.game.util.CsvUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class DatabaseLoaderRunner implements CommandLineRunner {

    public static final String PLACE = "PLACE";
    public static final String ROOM = "ROOM";
    public static final String SITUATION = "SITUATION";
    public static final String ACTION = "ACTION";
    public static final String CREATURE = "CREATURE";
    public static final String FIELD_SEPARATOR = ",";

    private final PlayerRepository playerRepository;
    private final PlaceRepository placeRepository;
    private final SituationRepository situationRepository;
    private final RoomRepository roomRepository;
    private final ActionRepository actionRepository;
    private final CreatureRepository creatureRepository;


    private List<String> listOfObject(Object o) {
        if (o != null) {
            return List.of(((String) o).split(FIELD_SEPARATOR));
        } else {
            return List.of();
        }
    }

    private void savePlacesFromCsv(List<List<String>> data) {

        this.placeRepository.deleteAll()
                .then(this.getFlux(data, PLACE)
                        .map(field -> Place.builder()
                                .id(field.get(1))
                                .type(PlaceType.valueOf(field.get(2)))
                                .minSize(Integer.valueOf(field.get(3)))
                                .maxSize(Integer.valueOf(field.get(4)))
                                .rarity(Rarity.builder().rarity(Integer.valueOf(field.get(5))).build())
                                .build())
                        .zipWith(this.getRarities(data, PLACE, 5),
                                (place, minMaxPerc) ->
                                        place.toBuilder()
                                                .rarity(
                                                        Rarity.builder()
                                                                .rarity(place.getRarity().getRarity())
                                                                .minRarity(minMaxPerc.getMin())
                                                                .maxRarity(minMaxPerc.getMax())
                                                                .build()
                                                )
                                                .build())
                        .collectList()
                        .flatMap(obj -> this.placeRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved places: {}", place));
    }

    private void saveRoomsFromCsv(List<List<String>> data) {
        this.roomRepository.deleteAll()
                .then(
                        this.getFlux(data, ROOM)
                                .map(field -> Room.builder()
                                        .id(field.get(1))
                                        .maxItems(Integer.valueOf(field.get(3)))
                                        .rarity(Rarity.builder().rarity(Integer.valueOf(field.get(4))).build())
                                        .usedInPlaces(listOfObject(field.get(5)))
                                        .build())
                                .zipWith(this.getRarities(data, ROOM, 4),
                                        (room, minMaxPerc) ->
                                                room.toBuilder()
                                                        .rarity(
                                                                Rarity.builder()
                                                                        .rarity(room.getRarity().getRarity())
                                                                        .minRarity(minMaxPerc.getMin())
                                                                        .maxRarity(minMaxPerc.getMax())
                                                                        .build()
                                                        )
                                                        .build())
                                .collectList()
                                .flatMap(obj -> this.roomRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved rooms: {}", place));
    }

    private void saveSituationsFromCsv(List<List<String>> data) {
        this.situationRepository.deleteAll()
                .then(
                        this.getFlux(data, SITUATION)
                                .map(field -> Situation.builder()
                                        .id(field.get(1))
                                        .type(SituationType.valueOf(field.get(2)))
                                        .minCreatures(Integer.valueOf(field.get(3)))
                                        .maxCreatures(Integer.valueOf(field.get(4)))
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
                        this.getFlux(data, ACTION)
                                .map(field -> Action.builder()
                                        .id(field.get(1))
                                        .type(ActionType.valueOf(field.get(2)))
                                        .usedInSituations(listOfObject(field.get(3)))
                                        .build())
                                .collectList()
                                .flatMap(obj -> this.actionRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved actions: {}", place));
    }

    private void saveCreaturesFromCsv(List<List<String>> data) {
        this.creatureRepository.deleteAll()
                .then(
                        this.getFlux(data, CREATURE)
                                .map(field -> Creature.builder()
                                        .id(field.get(1))
                                        .name(field.get(2))
                                        .difficult(MiniGameDifficult.valueOf(field.get(3)))
                                        .moveSpeed(Integer.valueOf(field.get(4)))
                                        .hpHead(Integer.valueOf(field.get(5)))
                                        .hpBody(Integer.valueOf(field.get(6)))
                                        .hpArms(Integer.valueOf(field.get(7)))
                                        .hpLegs(Integer.valueOf(field.get(8)))
                                        .build())
                                .collectList()
                                .flatMap(obj -> this.creatureRepository.saveAll(obj).collectList())
                )
                .subscribe(place -> log.info("#### Saved creatures: {}", place));
    }


    private Flux<MinMaxPerc> getRarities(List<List<String>> data, String objType, Integer rarityColumn) {
        final Integer maxRarity = Flux.fromStream(data.stream()
                .filter(line -> objType.equalsIgnoreCase(line.get(0))))
                .map(field -> Integer.valueOf(field.get(rarityColumn)))
                .reduce((integer, integer2) -> (integer > integer2) ? integer : integer2)
                .block();

        final Integer sumRarity = Flux.fromStream(data.stream()
                .filter(line -> objType.equalsIgnoreCase(line.get(0))))
                .map(field -> Integer.valueOf(field.get(rarityColumn)))
                .map(integer -> maxRarity - integer + 1)//inverse the value
                .reduce((integer, integer2) -> integer + integer2)
                .block();

        final List<MinMaxPerc> rarities = new ArrayList<>();

        Flux.fromStream(data.stream()
                .filter(line -> objType.equalsIgnoreCase(line.get(0))))
                .map(field -> Integer.valueOf(field.get(rarityColumn)))
                .map(integer -> (maxRarity - integer + 1d) / sumRarity)//inverse the value
                .map(value -> MinMaxPerc.builder()
                        .min(rarities.isEmpty() ? 0d : rarities.get(rarities.size() - 1).getMax())
                        .max(rarities.isEmpty() ? value : rarities.get(rarities.size() - 1).getMax() + value)
                        .build())
                .map(minMaxPerc -> rarities.add(minMaxPerc))
                .subscribe();

        return Flux.fromIterable(rarities);
    }

    private Flux<List<String>> getFlux(List<List<String>> data, String objType) {
        return Flux.fromStream(data.stream()
                .filter(line -> objType.equalsIgnoreCase(line.get(0))));
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

        File file = ResourceUtils.getFile("classpath:database/database.csv");

        List<List<String>> data = CsvUtil.getCsvFile(file);

        this.savePlacesFromCsv(data);
        this.saveRoomsFromCsv(data);
        this.saveSituationsFromCsv(data);
        this.saveActionsFromCsv(data);
        this.saveCreaturesFromCsv(data);


    }


    @Builder
    @Getter
    static class MinMaxPerc {
        private Double min;
        private Double max;

    }


}
