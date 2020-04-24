package com.game.model.action.executor;

import com.game.model.action.ActionExecutor;
import com.game.model.minigame.MiniGame;

public class NextRoomExecutor implements ActionExecutor {

    @Override
    public MiniGame apply(final MiniGame miniGame) {

        miniGame.createNewRoomByPlace();

        return miniGame;
    }
}
