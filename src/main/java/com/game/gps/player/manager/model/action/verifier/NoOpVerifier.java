package com.game.gps.player.manager.model.action.verifier;

import com.game.gps.player.manager.model.action.ActionVerifier;
import com.game.gps.player.manager.model.minigame.MiniGame;

public class NoOpVerifier implements ActionVerifier {
    @Override
    public Boolean apply(MiniGame miniGame) {
        return Boolean.TRUE;
    }
}
