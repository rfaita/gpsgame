package com.game.model.minigame;

import com.game.model.EventGenerated;
import com.game.model.Player;
import com.game.model.Situation;
import com.game.model.action.ActionType;
import com.game.model.minigame.representation.MiniGameRepresentation;
import com.game.model.type.ActionResultType;
import com.game.util.ListUtil;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.stream.Collectors;

@Document
@Builder
public class MiniGame {

    @Id
    private String id;
    private String playerId;
    private EventGenerated eventGenerated;
    private MiniGameDifficult difficult;

    private MiniGameState currentState;

    private MiniGameDataCache dataCache;

    private List<MiniGameState> stateHistory;

    private List<MiniGameState.ActionResult> actionResultsCache;

    private void saveCurrentState() {
        if (this.currentState != null) {
            this.stateHistory = ListUtil.concat(this.stateHistory, this.currentState);
        }
    }

    private void changeCurrentState(MiniGameState.MiniGameStateBuilder miniGameStateBuilder) {
        this.saveCurrentState();
        this.currentState = miniGameStateBuilder
                .lastActionResults(this.actionResultsCache != null ? List.copyOf(this.actionResultsCache) : null)
                .build();
        this.clearActionResultsCache();
    }

    public MiniGame dataCache(MiniGameDataCache dataCache) {
        this.dataCache = dataCache;
        return this;
    }

    private void addActionResultCache(MiniGameState.ActionResult actionResult) {
        if (this.actionResultsCache == null) {
            this.actionResultsCache = new ArrayList<>();
        }
        this.actionResultsCache.add(actionResult);
    }

    private void clearActionResultsCache() {
        this.actionResultsCache = new ArrayList<>();
    }

    public String getPlayerId() {
        return playerId;
    }

    public MiniGame start(final Player player) {

        this.playerId = player.getId();

        this.difficult = MiniGameDifficult.MEDIUM;

        MiniGameState.Situation nextSituation = this.eventGenerated.getFirstSituation()
                .toSituation()
                .toMiniGameStateSituation();

        this.changeCurrentState(
                MiniGameState.builder()
                        .player(player)
                        .currentSituation(nextSituation)
                        .currentCreatures(this.createCreatures(nextSituation.getMaxCreatures()))
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
        );
        return this;
    }

    public MiniGame executeAction(final String actionId) {

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .lastAction(getActionById(actionId))
        );

        return this.currentState.getLastAction().getType().runAllExecutors(this);
    }


    public void createNewRoomByPlace() {

        MiniGameState.Room nextRoom
                = this.dataCache.getRandomRoomByPlaceId(this.eventGenerated.getPlace().getId());
        MiniGameState.Situation nextSituation = this.dataCache.getRandomSituationByRoomId(nextRoom.getId());


        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentRoom(nextRoom)
                        .currentSituation(nextSituation)
                        .currentCreatures(this.createCreatures(nextSituation.getMaxCreatures()))
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
        );
    }

    public void createNewSituationBySituation() {

        Situation nextSituation
                = this.dataCache.getRandomSituationBySituationId(this.currentState.getCurrentSituation().getId());


        List<MiniGameState.Creature> currentCreatures = this.currentState.getCurrentCreatures();

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentSituation(nextSituation.toMiniGameStateSituation())
                        .currentCreatures(ListUtil.concat(currentCreatures, this.createCreatures(nextSituation.getMaxCreatures())))
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
        );

    }


    public void creaturesTurn() {

        Player player = currentPlayerState();

        List<MiniGameState.Creature> creatures =
                this.currentState.getCurrentCreatures().stream()
                        .map(MiniGameState.Creature::move)
                        .map(creature -> creature.attackIfPossible(player))
                        .collect(Collectors.toList());

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentCreatures(creatures)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
        );

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
        );
    }

    public void pushAllCloserCreatures() {
        List<MiniGameState.Creature> creatures =
                this.currentState.getCurrentCreatures().stream()
                        .map(MiniGameState.Creature::knockedDown)
                        .collect(Collectors.toList());


        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentCreatures(creatures)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
        );
    }

    public void damageCloserEnemy(Integer damage) {

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
        );

    }

    public void spawnEnemy() {

        this.changeCurrentState(
                this.currentState.toBuilder()
                        .currentCreatures(createCreatures(this.difficult.getDifficult()))
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
        );

    }

    private List<MiniGameState.Creature> createCreatures(final int amount) {

        List<MiniGameState.Creature> spawnedCreatures = new ArrayList<>(amount);

        Map<String, Integer> aggregateResult = new HashMap<>();

        for (int i = 1; i <= amount; i++) {
            MiniGameState.Creature creature = this.dataCache.getRandomCreatureByDifficult(this.difficult);
            creature.addObserver(this::addActionResultCache);

            aggregateResult.put(creature.getName(), aggregateResult.getOrDefault(creature.getName(), 0) + 1);

            spawnedCreatures.add(creature);
        }

        aggregateResult.entrySet().stream()
                .forEach(entry ->
                        this.addActionResultCache(MiniGameState.ActionResult.builder()
                                .type(ActionResultType.ENEMY_SPAWN)
                                .args(List.of(entry.getKey(), String.valueOf(entry.getValue())))
                                .build())
                );


        return spawnedCreatures;
    }


    public MiniGame loadObservers() {
        this.currentState.getCurrentCreatures().stream()
                .forEach(creature -> creature.addObserver(this::addActionResultCache));

        return this;
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

    public MiniGameRepresentation toMiniGameRepresentation() {
        return MiniGameRepresentation.builder()
                .id(this.id)
                .playerId(this.getPlayerId())
                .eventGenerated(this.eventGenerated)
                .difficult(this.difficult)
                .currentState(this.currentState.toMiniGameStateRepresentation())
                .stateHistory(this.stateHistory != null ? this.stateHistory.stream()
                        .map(MiniGameState::toMiniGameStateRepresentation)
                        .collect(Collectors.toList()) : null)
                .build();
    }


}
