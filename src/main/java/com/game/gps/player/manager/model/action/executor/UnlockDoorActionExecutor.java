package com.game.gps.player.manager.model.action.executor;

import com.game.gps.player.manager.model.action.ActionExecutor;
import com.game.gps.player.manager.model.minigame.MiniGame;

public class UnlockDoorActionExecutor implements ActionExecutor {

    @Override
    public MiniGame apply(final MiniGame miniGame) {

        miniGame.createNewRoomByPlace();

        return miniGame;
    }
}
