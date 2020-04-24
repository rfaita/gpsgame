package com.game.model.action;

import com.game.model.minigame.MiniGame;

import java.util.concurrent.ThreadLocalRandom;

@FunctionalInterface
public interface ActionExecutor {

    MiniGame apply(final MiniGame miniGame);

    static ActionExecutor and(final ActionExecutor actionVerifier, final ActionExecutor actionExecutor2) {
        return miniGame -> actionExecutor2.apply(actionVerifier.apply(miniGame));
    }


    default Double random(Double maxValue) {
        return random(0d, maxValue);
    }

    default Double randomPercentage() {
        return random(0d, 100d);
    }

    default Double random(Double minValue, Double maxValue) {
        return ThreadLocalRandom.current().nextDouble(minValue, maxValue + 0.00000001d);

    }

}
