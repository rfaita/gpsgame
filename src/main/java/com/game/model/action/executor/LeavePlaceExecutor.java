package com.game.model.action.executor;

import com.game.model.action.ActionExecutor;
import com.game.model.minigame.MiniGame;

public class LeavePlaceExecutor implements ActionExecutor {
    @Override
    public MiniGame apply(MiniGame miniGame) {
        miniGame.end();
        return miniGame;
    }
}
