package com.game.gps.player.manager.model.action.executor;

import com.game.gps.player.manager.model.action.ActionExecutor;
import com.game.gps.player.manager.model.minigame.MiniGame;

public class NoOpExecutor implements ActionExecutor {

    @Override
    public MiniGame apply(final MiniGame miniGame) {
        return miniGame;
    }
}
