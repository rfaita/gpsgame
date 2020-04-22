package com.game.gps.player.manager.model.action;

import com.game.gps.player.manager.model.minigame.MiniGame;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public enum ActionType {

    A(null, null);

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

    public Mono<Boolean> runAllVerifiers(final MiniGame miniGame) {
        return Flux.just(this.getActionVerifiers().toArray(new ActionVerifier[]{}))
                .reduce(Boolean.TRUE, (aBoolean, actionVerifier) -> aBoolean &= actionVerifier.apply(miniGame));

    }

    public Mono<MiniGame> runAllExecutors(final MiniGame miniGame) {
        return Flux.just(this.getActionExecutors().toArray(new ActionExecutor[]{}))
                .reduce(miniGame, (newMiniGame, actionExecuter) -> actionExecuter.apply(newMiniGame));

    }
}
