package com.game.model.minigame;

import com.game.model.*;
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

    public MiniGameState.Room getRandomRoomByPlaceId(String placeId) {
        return this.allRooms.stream()
                .filter(room -> room.getUsedInPlaces().contains(placeId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .map(Room::toMiniGameStateRoom)
                .get();
    }

    public MiniGameState.Situation getRandomSituationByPlaceId(String placeId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInPlaces().contains(placeId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .map(Situation::toMiniGameStateSituation)
                .get();
    }

    public MiniGameState.Situation getRandomSituationByRoomId(String roomId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInRooms().contains(roomId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .map(Situation::toMiniGameStateSituation)
                .get();
    }

    public MiniGameState.Creature getRandomCreatureByDifficult(MiniGameDifficult difficult) {
        return this.allCreatures.stream()
                .filter(creature -> creature.getDifficult().equals(difficult))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .map(Creature::toMiniGameStateCreature)
                .get();
    }


    public Situation getRandomSituationBySituationId(String situationId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInSituations().contains(situationId))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    public List<MiniGameState.Action> getAllActionsBySituationId(String situationId) {
        return this.allActions.stream()
                .filter(action -> action.getUsedInSituations().contains(situationId))
                .map(Action::toMiniGameStateAction)
                .collect(Collectors.toList());
    }


}
