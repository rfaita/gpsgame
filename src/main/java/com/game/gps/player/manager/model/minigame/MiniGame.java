package com.game.gps.player.manager.model.minigame;

import com.game.gps.player.manager.model.*;
import com.game.gps.player.manager.model.action.ActionType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Document
@Builder
@Getter//Remove
public class MiniGame {

    @Id
    private String id;
    private EventGenerated eventGenerated;

    private MiniGameState currentState;

    private MiniGameDataCache dataCache;

    private final List<MiniGameState> stateHistory = new ArrayList<>();

    private void saveCurrentState() {
        if (this.currentState != null) {
            this.stateHistory.add(this.currentState);
        }
    }

    private void setCurrentState(MiniGameState miniGameState) {
        this.saveCurrentState();
        this.currentState = miniGameState;
    }

    private MiniGameState getCurrentState() {
        return this.currentState;
    }

    public MiniGame dataCache(MiniGameDataCache dataCache) {
        this.dataCache = dataCache;
        return this;
    }

    public MiniGame clearDataCache() {
        this.dataCache = null;
        return this;
    }

    public MiniGame start(final Player player) {

        Situation nextSituation = this.eventGenerated.getFirstSituation().toSituation();

        this.setCurrentState(
                MiniGameState.builder()
                        .player(player)
                        .currentSituation(nextSituation)
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build()
        );
        return this;
    }

    public Mono<MiniGame> executeAction(final String actionId) {

        this.setCurrentState(
                this.getCurrentState().toBuilder()
                        .currentAction(getActionById(actionId))
                        .build());

        return Mono.just(this.getCurrentState().getCurrentAction().getType().runAllExecutors(this));
    }


    public void createNewRoomByPlace() {

        Room nextRoom
                = this.dataCache.getRandomRoomByPlaceId(this.eventGenerated.getPlace().getId());
        Situation nextSituation = this.dataCache.getRandomSituationByRoomId(nextRoom.getId());

        this.setCurrentState(
                this.getCurrentState().toBuilder()
                        .currentRoom(nextRoom)
                        .currentSituation(nextSituation)
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build());
    }

    public void createNewSituationBySituation() {

        Situation nextSituation
                = this.dataCache.getRandomSituationBySituationId(this.currentState.getCurrentSituation().getId());

        this.setCurrentState(
                this.getCurrentState().toBuilder()
                        .currentSituation(nextSituation)
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build());

    }

    private Action getActionById(final String actionId) {
        return this.currentState.getCurrentActions().stream()
                .filter(action -> actionId.equals(action.getId()))
                .findFirst().orElse(Action.builder().type(ActionType.NO_OP).build());
    }

    private List<Action> getAllActionsBySituationId(final String situationId) {
        return this.dataCache.getAllActionsBySituationId(situationId).stream()
                .filter(action -> action.getType().runAllVerifiers(MiniGame.this))
                .collect(Collectors.toList());
    }

}
