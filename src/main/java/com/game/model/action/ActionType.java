package com.game.model.action;

import com.game.model.action.executor.*;
import com.game.model.action.verifier.*;
import com.game.model.minigame.MiniGame;

import java.util.List;

public enum ActionType {

    LEAVE_PLACE(new HasNoEnemiesVerifier(), new LeavePlaceExecutor()),
    UNLOCK_DOOR(new NoOpVerifier(), new NextRoomExecutor()),
    KICK_DOOR(new NoOpVerifier(), new NextRoomExecutor()),
    SHOOT_DOOR_LOCKER(List.of(new HasRangedWeaponEquippedVerifier(), new HasAmmoRangedWeaponVerifier()), new NextRoomExecutor()),
    ENTER_PLACE(List.of(new HasNoEnemiesVerifier(), new IsTheFirstSituation()), new NextRoomExecutor()),
    RUN_ENTER_PLACE(List.of(new HasEnemiesVerifier(), new IsTheFirstSituation()), new NextRoomExecutor()),
    NEXT_ROOM(List.of(new HasNoEnemiesVerifier(), new NotIsTheFirstSituation()), new NextRoomExecutor()),
    RUN_NEXT_ROOM(List.of(new HasEnemiesVerifier(), new NotIsTheFirstSituation()), new NextRoomExecutor()),
    WAIT(new HasEnemiesVerifier(), new CreaturesTurnExecutor()),
    PUSH_CREATURES(new HasCloseEnemiesVerifier(), List.of(new PushAllEnemyExecutor(), new CreaturesTurnExecutor())),
    MELEE_ATTACK(new HasCloseEnemiesVerifier(), List.of(new AttackEnemyExecutor(), new EnemiesSpawnExecutor(), new CreaturesTurnExecutor())),
    SHOOT_ATTACK(List.of(new HasEnemiesVerifier(), new HasRangedWeaponEquippedVerifier(), new HasAmmoRangedWeaponVerifier()),
            List.of(new AttackEnemyExecutor(), new EnemiesSpawnExecutor(), new CreaturesTurnExecutor()));

    private final List<ActionVerifier> actionVerifiers;
    private final List<ActionExecutor> actionExecutors;

    ActionType(ActionVerifier actionVerifier, ActionExecutor actionExecutor) {
        this.actionVerifiers = List.of(actionVerifier);
        this.actionExecutors = List.of(actionExecutor);
    }

    ActionType(List<ActionVerifier> actionVerifiers, ActionExecutor actionExecutor) {
        this.actionVerifiers = actionVerifiers;
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
