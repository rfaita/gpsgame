package com.game.model.minigame;

import com.game.model.*;
import com.game.util.RandomUtil;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        final Double randomRarity = RandomUtil.random(1d);

        List<Room> filteredRooms = this.allRooms.stream()
                .filter(room -> room.getUsedInPlaces().contains(placeId))
                .collect(Collectors.toList());

        List<HasRarity> filteredRarities = new ArrayList<>(filteredRooms);

        Map<String, Rarity> rarities = Rarity.calculateRarities(filteredRarities);

        return filteredRooms.stream()
                .filter(room -> rarities.get(room.getId()).getMinRarity() <= randomRarity
                        && randomRarity < rarities.get(room.getId()).getMaxRarity())
                .findFirst()
                .map(Room::toMiniGameStateRoom)
                .get();
    }

    public MiniGameState.Situation getRandomSituationByPlaceId(String placeId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInPlaces().contains(placeId))
                .sorted((o1, o2) -> RandomUtil.random(-1, 1))
                .findAny()
                .map(situation -> situation.toEventGeneratedSituation().toMiniGameStateSituation())
                .get();
    }

    public MiniGameState.Situation getRandomSituationByRoomId(String roomId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInRooms().contains(roomId))
                .sorted((o1, o2) -> RandomUtil.random(-1, 1))
                .findAny()
                .map(situation -> situation.toEventGeneratedSituation().toMiniGameStateSituation())
                .get();
    }

    public MiniGameState.Creature getRandomCreatureByDifficult(MiniGameDifficult difficult) {
        return this.allCreatures.stream()
                .filter(creature -> creature.getDifficult().equals(difficult))
                .sorted((o1, o2) -> RandomUtil.random(-1, 1))
                .findAny()
                .map(Creature::toMiniGameStateCreature)
                .get();
    }


    public Situation getRandomSituationBySituationId(String situationId) {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInSituations().contains(situationId))
                .sorted((o1, o2) -> RandomUtil.random(-1, 1))
                .findAny()
                .get();
    }

    public List<MiniGameState.Action> getAllActionsBySituationId(String situationId) {
        return this.allActions.stream()
                .filter(action -> action.getUsedInSituations().contains(situationId)
                        || action.getUsedInSituations().contains("*"))
                .map(Action::toMiniGameStateAction)
                .collect(Collectors.toList());
    }


}
