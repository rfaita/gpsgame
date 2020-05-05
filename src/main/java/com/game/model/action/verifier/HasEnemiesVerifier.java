package com.game.model.action.verifier;

import com.game.model.action.ActionVerifier;
import com.game.model.minigame.MiniGame;

public class HasEnemiesVerifier implements ActionVerifier {
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
                .count() > 0;
    }
}
