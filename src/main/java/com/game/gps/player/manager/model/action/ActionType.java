package com.game.gps.player.manager.model.action;

import com.game.gps.player.manager.model.action.executor.NoOpExecutor;
import com.game.gps.player.manager.model.action.executor.UnlockDoorActionExecutor;
import com.game.gps.player.manager.model.action.verifier.NoOpVerifier;
import com.game.gps.player.manager.model.minigame.MiniGame;

import java.util.List;

public enum ActionType {

    NO_OP(new NoOpVerifier(), new NoOpExecutor()),
    NEXT_DOOR(new NoOpVerifier(), new UnlockDoorActionExecutor());

    private final List<ActionVerifier> actionVerifiers;
    private final List<ActionExecutor> actionExecutors;

    ActionType(ActionVerifier actionVerifier, ActionExecutor actionExecutor) {
        this.actionVerifiers = List.of(actionVerifier);
        this.actionExecutors = List.of(actionExecutor);
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
