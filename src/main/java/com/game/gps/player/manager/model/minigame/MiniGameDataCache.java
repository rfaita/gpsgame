package com.game.gps.player.manager.model.minigame;

import com.game.gps.player.manager.model.*;
import lombok.Builder;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Builder
public class MiniGameDataCache {

    private List<Room> allRooms;
    private List<Situation> allSituations;
    private List<Action> allActions;
    private List<Item> allItems;
    private List<Creature> allCreatures;
    private List<Survivor> allSurvivor;

    public Room getRandomRoomByPlaceId(String placeId) {
        return this.allRooms.stream()
                .filter(room -> room.getUsedInPlaces().contains(placeId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    public Situation getRandomSituationByPlaceId(String placeId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInPlaces().contains(placeId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    public Situation getRandomSituationByRoomId(String roomId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInRooms().contains(roomId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    public Situation getRandomSituationBySituationId(String situationId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInSituations().contains(situationId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    public List<Action> getAllActionsBySituationId(String situationId) {
        return this.allActions.stream()
                .filter(action -> action.getUsedInSituations().contains(situationId))
                .collect(Collectors.toList());
    }


}
