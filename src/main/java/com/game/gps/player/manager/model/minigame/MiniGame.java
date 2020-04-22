package com.game.gps.player.manager.model.minigame;

import com.game.gps.player.manager.model.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@Getter
@Setter
public class MiniGame {

    private String id;
    private EventGenerated eventGenerated;
    private SituationGenerated firstSituation;
    private Player player;

    private Room currentRoom;
    private SituationGenerated currentSituation;
    private Action currentAction;
    private List<Action> currentActions;


    private List<Room> allRooms;
    private List<Situation> allSituations;
    private List<Action> allActions;
    private List<Item> allItems;

    private List<Decision> decisions;





    public List<Action> getCurrentActions() {
        return this.currentActions;
    }

    public Mono<MiniGame> executeAction(String actionId) {
        Optional<Action> optionalAction = this.currentActions.stream()
                .filter(action -> actionId.equals(action.getId()))
                .findFirst();

        if (optionalAction.isPresent()) {
            this.currentAction = optionalAction.get();
            this.currentAction.getActionType().runAllExecutors(this);
        }

        return Mono.just(this);
    }

    private Room getRandomRoomByPlaceId() {
        return this.allRooms.stream()
                .filter(room -> room.getUsedInPlaces().contains(this.eventGenerated.getPlace().getId()))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    private Situation getRandomSituationByRoomId() {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInRooms().contains(this.currentRoom.getId()))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    private Situation getRandomSituationBySituationId() {
        return this.allSituations.stream()
                .filter(situation -> situation.getUsedInSituations().contains(this.currentSituation.getId()))
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }

    private List<Action> getAllActionsBySituationId() {
        return this.allActions.stream()
                .filter(action -> action.getUsedInSituations().contains(this.currentSituation.getId()))
                .collect(Collectors.toList());
    }


    public void createNewRoomByPlace() {

        this.currentRoom = this.getRandomRoomByPlaceId();
        //cast
        //this.currentSituation = this.getRandomSituationByRoomId();
        this.currentActions = this.getAllActionsBySituationId();

    }

    public void createNewSituationBySituation() {

        //this.currentSituation = this.getRandomSituationBySituationId();
        this.currentActions = this.getAllActionsBySituationId();

    }

}
