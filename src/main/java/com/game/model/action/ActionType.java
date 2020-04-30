package com.game.model.action;

import com.game.model.action.executor.*;
import com.game.model.action.verifier.NoOpVerifier;
import com.game.model.minigame.MiniGame;

import java.util.List;

public enum ActionType {

    NO_OP(new NoOpVerifier(), new NoOpExecutor()),
    NEXT_ROOM(new NoOpVerifier(), new NextRoomExecutor()),
    PUSH_ALL_CREATURES(new NoOpVerifier(), List.of(new PushAllEnemyExecutor(), new CreaturesTurnExecutor())),
    ATTACK_CLOSER_CREATURE(new NoOpVerifier(), List.of(new AttackEnemyExecutor(), new EnemiesSpawnExecutor(), new CreaturesTurnExecutor()));

    private final List<ActionVerifier> actionVerifiers;
    private final List<ActionExecutor> actionExecutors;

    ActionType(ActionVerifier actionVerifier, ActionExecutor actionExecutor) {
        this.actionVerifiers = List.of(actionVerifier);
        this.actionExecutors = List.of(actionExecutor);
    }

    ActionType(ActionVerifier actionVerifier, List<ActionExecutor> actionExecutors) {
        this.actionVerifiers = List.of(actionVerifier);
        this.actionExecutors = actionExecutors;
    }

    ActionType(List<ActionVerifier> actionVerifiers, List<ActionExecutor> actionExecutors) {
        this.actionVerifiers = actionVerifiers;
        this.actionExecutors = actionExecutors;
    }

    public List<ActionVerifier> getActionVerifiers() {
        return actionVerifiers;
    }

    public List<ActionExecutor> getActionExecutors() {
        return actionExecutors;
    }

    public Boolean runAllVerifiers(final MiniGame miniGame) {
        return this.getActionVerifiers().stream()
                .map(actionVerifier -> actionVerifier.apply(miniGame))
                .reduce(Boolean::logicalAnd)
                .get();

    }

    public MiniGame runAllExecutors(final MiniGame miniGame) {
        return this.getActionExecutors().stream()
                .reduce((actionExecutor, actionExecutor2) -> ActionExecutor.and(actionExecutor, actionExecutor2))
                .get()
                .apply(miniGame);

    }
}
