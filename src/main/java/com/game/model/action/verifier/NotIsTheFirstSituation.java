package com.game.model.action.verifier;

import com.game.model.action.ActionVerifier;
import com.game.model.minigame.MiniGame;

public class NotIsTheFirstSituation implements ActionVerifier {
    @Override
    public Boolean apply(MiniGame miniGame) {
        return miniGame
                .getCurrentState() != null
                && miniGame
                .getCurrentState()
                .getCurrentRoom() != null;
    }
}
