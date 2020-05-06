package com.game.model.minigame;

import com.game.exception.GenericException;
import com.game.model.EventGenerated;
import com.game.model.Player;
import com.game.model.Situation;
import com.game.model.minigame.representation.MiniGameRepresentation;
import com.game.model.type.ActionResultType;
import com.game.util.ListUtil;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.stream.Collectors;

import static com.game.exception.ExceptionMessageConstant.ACTION_NOT_FOUND;

@Document
@Builder
public class MiniGame {

    @Id
    private String id;
    private Stage stage;
    private String playerId;
    private EventGenerated eventGenerated;
    private MiniGameDifficult difficult;

    private MiniGameState currentState;

    private MiniGameDataCache dataCache;

    private List<MiniGameState> tempStates;

    private List<MiniGameState> stateHistory;

    private List<MiniGameState.ActionResult> actionResultsCache;

    private void saveCurrentState() {
        if (this.currentState != null) {
            this.stateHistory = ListUtil.concat(this.stateHistory, this.currentState);
        }
    }

    private MiniGame changeCurrentState() {
        this.saveCurrentState();
        this.currentState = this.tempStates.stream().reduce(MiniGameState::merge).get();
        //actions verifiers must be executed AFTER the chance of state,
        //because we need the result of last action to filter the new current actions
        this.currentState = this.currentState.toBuilder()
                .currentActions(this.currentState.getCurrentActions().stream()
                        .filter(action -> action.getType().runAllVerifiers(MiniGame.this))
                        .collect(Collectors.toList()))
                .build();
        return this;
    }

    private void addTempState(final MiniGameState tempState) {

        MiniGameState finaTempState = tempState.toBuilder()
                .lastActionResults(this.actionResultsCache != null ? List.copyOf(this.actionResultsCache) : null)
                .build();
        this.tempStates = ListUtil.concat(this.tempStates, finaTempState);

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

        this.stage = Stage.RUNNING;

        this.difficult = MiniGameDifficult.MEDIUM;

        MiniGameState.Situation nextSituation = this.eventGenerated.getFirstSituation().toMiniGameStateSituation();

        this.addTempState(
                MiniGameState.builder()
                        .player(player)
                        .currentSituation(nextSituation)
                        .currentCreatures(this.createCreatures(nextSituation.getNumberOfCreatures()))
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build()
        );
        return changeCurrentState();
    }

    public void end() {
        this.currentState = null;
        this.stage = Stage.DONE;

    }

    public MiniGame executeAction(final String actionId) {

        MiniGameState.Action action = getActionById(actionId);

        this.addTempState(
                this.currentState.toBuilder()
                        .lastAction(action)
                        .build()
        );

        action.getType().runAllExecutors(this);
        return changeCurrentState();
    }


    public void createNewRoomByPlace() {

        MiniGameState.Room nextRoom
                = this.dataCache.getRandomRoomByPlaceId(this.eventGenerated.getPlace().getId());
        MiniGameState.Situation nextSituation = this.dataCache.getRandomSituationByRoomId(nextRoom.getId());


        this.addTempState(
                this.currentState.toBuilder()
                        .currentRoom(nextRoom)
                        .currentSituation(nextSituation)
                        .currentCreatures(this.createCreatures(nextSituation.getNumberOfCreatures()))
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build()
        );
    }

    public void createNewSituationBySituation() {

        Situation nextSituation
                = this.dataCache.getRandomSituationBySituationId(this.currentState.getCurrentSituation().getId());


        List<MiniGameState.Creature> currentCreatures = this.currentState.getCurrentCreatures();

        this.addTempState(
                this.currentState.toBuilder()
                        .currentSituation(nextSituation.toEventGeneratedSituation().toMiniGameStateSituation())
                        .currentCreatures(ListUtil.concat(currentCreatures, this.createCreatures(nextSituation.getMaxCreatures())))
                        .currentActions(this.getAllActionsBySituationId(nextSituation.getId()))
                        .build()
        );

    }


    public void creaturesTurn() {

        Player player = getCurrentPlayerState();

        List<MiniGameState.Creature> creatures =
                this.currentState.getCurrentCreatures().stream()
                        .map(creature -> creature.doAction(player))
                        .collect(Collectors.toList());

        this.addTempState(
                this.currentState.toBuilder()
                        .currentCreatures(creatures)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build()
        );

    }

    public EventGenerated getEventGenerated() {
        return this.eventGenerated;
    }

    public MiniGameState getCurrentState() {
        return this.currentState;
    }

    public Player getCurrentPlayerState() {
        //TODO: return only a COPY of player
        return this.currentState.getPlayer();
    }

    public void damagePlayer(Integer damage) {

        Player player = this.getCurrentPlayerState();

        player.damage(damage);

        this.addTempState(
                this.currentState.toBuilder()
                        .player(player)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build()
        );
    }

    public void pushAllCloserCreatures() {
        List<MiniGameState.Creature> creatures =
                this.currentState.getCurrentCreatures().stream()
                        .map(MiniGameState.Creature::knockedDown)
                        .collect(Collectors.toList());


        this.addTempState(
                this.currentState.toBuilder()
                        .currentCreatures(creatures)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build()
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

        this.addTempState(
                this.currentState.toBuilder()
                        //.currentCreatures(creatures)
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build()
        );

    }

    public void spawnEnemy() {

        this.addTempState(
                this.currentState.toBuilder()
                        .currentCreatures(createCreatures(this.difficult.getDifficult()))
                        .currentActions(this.getAllActionsBySituationId(this.currentState.getCurrentSituation().getId()))
                        .build()
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
                .findFirst().orElseThrow(() -> GenericException.of(ACTION_NOT_FOUND, actionId));
    }

    private List<MiniGameState.Action> getAllActionsBySituationId(final String situationId) {
        return this.dataCache.getAllActionsBySituationId(situationId)
                .stream()
                .collect(Collectors.toList());
    }

    public MiniGameRepresentation toMiniGameRepresentation() {
        return MiniGameRepresentation.builder()
                .id(this.id)
                .stage(this.stage)
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
