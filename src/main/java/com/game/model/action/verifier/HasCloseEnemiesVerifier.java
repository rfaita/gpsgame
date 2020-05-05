package com.game.model.action.verifier;

import com.game.model.action.ActionVerifier;
import com.game.model.minigame.MiniGame;

public class HasCloseEnemiesVerifier implements ActionVerifier {
    @Override
    public Boolean apply(MiniGame miniGame) {
        return miniGame
                .currentState() != null
                && miniGame
                .currentState()
                .getCurrentCreatures() != null
                && miniGame
                .currentState()
                .getCurrentCreatures().stream()
                .filter(creature -> creature.getDistance() <= 0).count() > 0
                && miniGame
                .currentState()
                .getCurrentCreatures().stream()
                .filter(creature -> creature.getKnockedDown() <= 0).count() > 0;
    }
}
