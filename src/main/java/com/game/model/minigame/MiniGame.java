package com.game.model.minigame;

import com.game.model.EventGenerated;
import com.game.model.Player;
import com.game.model.Situation;
import com.game.model.action.ActionType;
import com.game.util.ListUtil;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Document
@Builder
@Getter//Remove
public class MiniGame {

    @Id
    private String id;
    private String playerId;
    private EventGenerated eventGenerated;

    private MiniGameState currentState;

    private MiniGameDataCache dataCache;

    private List<MiniGameState> stateHistory;

    private void saveCurrentState() {
        if (this.currentState != null) {
            this.stateHistory = ListUtil.concat(this.stateHistory, this.currentState);
        }
    }

    private void changeCurrentState(MiniGameState miniGameState) {
        this.saveCurrentState();
        this.currentState = miniGameState;
    }

    public MiniGame dataCache(MiniGameDataCache dataCache) {
        this.dataCache = dataCache;
        return this;
    }

    public MiniGame clearDataCache() {
        this.dataCache = null;
        return this;
    }

    public MiniGame clearStateHistory() {
        this.stateHistory = null;
        return this;
    }

    public MiniGame start(final Player player) {

        this.playerId = player.getId();

        MiniGameState.Situation nextSituation = this.eventGenerated.getFirstSituation()
                .toSituation()
                .toMiniGameStateSituation();

        this.changeCurrentState(
                MiniGameState.builder()
                        .player(player)
                        .currentSituation(nextSituation)
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build()
        );
        return this;
    }

    public MiniGame executeAction(final String actionId) {

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .lastAction(getActionById(actionId))
                        .build());

        return this.currentState.getLastAction().getType().runAllExecutors(this);
    }

    public void moveCreatures() {

        List<MiniGameState.Creature> creatures =
                this.currentState.getCurrentCreatures().stream()
                        .map(creature -> creature.move())
                        .collect(Collectors.toList());

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentCreatures(creatures)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build());
    }

    public Player currentPlayerState() {
        //TODO: return only a COPY of player
        return this.currentState.getPlayer();
    }

    public void damagePlayer(Integer damage) {

        Player player = this.currentPlayerState();

        player.damage(damage);

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .player(player)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build()
        );
    }

    public void damageClosiestEnemy(Integer damage) {

        //dmg to enemy

        MiniGameState.Creature creature =
                this.currentState.getCurrentCreatures().stream()
                        .sorted(Comparator.comparing(MiniGameState.Creature::getDistance))
                        .findFirst()
                        .get();


        //remover the creature from current creatures and generate a new state with that
//        if (morreu) {
//            removeClosiestEnemy();
//        }

        this.changeCurrentState(
                this.currentState.toBuilder()
                        //.currentCreatures(creatures)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build()
        );

    }

    public void spawnEnemy() {

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build());

    }

    public void spawnEnemyBehind() {

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build());

    }

    public void createNewRoomByPlace() {

        MiniGameState.Room nextRoom
                = this.dataCache.getRandomRoomByPlaceId(this.eventGenerated.getPlace().getId());
        MiniGameState.Situation nextSituation = this.dataCache.getRandomSituationByRoomId(nextRoom.getId());

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentRoom(nextRoom)
                        .currentSituation(nextSituation)
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build());
    }

    public void createNewSituationBySituation() {

        Situation nextSituation
                = this.dataCache.getRandomSituationBySituationId(this.currentState.getCurrentSituation().getId());

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentSituation(nextSituation.toMiniGameStateSituation())
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build());

    }

    private MiniGameState.Action getActionById(final String actionId) {
        return this.currentState.getCurrentActions().stream()
                .filter(action -> actionId.equals(action.getId()))
                .findFirst().orElse(MiniGameState.Action.builder().type(ActionType.NO_OP).build());
    }

    private List<MiniGameState.Action> getAllActionsBySituationId(final String situationId) {
        return this.dataCache.getAllActionsBySituationId(situationId).stream()
                .filter(action -> action.getType().runAllVerifiers(MiniGame.this))
                .collect(Collectors.toList());
    }


}
