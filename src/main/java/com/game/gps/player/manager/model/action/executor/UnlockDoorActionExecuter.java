package com.game.gps.player.manager.model.action.executor;

import com.game.gps.player.manager.model.action.ActionExecutor;
import com.game.gps.player.manager.model.minigame.MiniGame;

public class UnlockDoorActionExecuter implements ActionExecutor {

    @Override
    public MiniGame apply(final MiniGame miniGame) {

        if (50 < 60) {
            miniGame.createNewRoomByPlace();
        }

        return miniGame;
    }
}
