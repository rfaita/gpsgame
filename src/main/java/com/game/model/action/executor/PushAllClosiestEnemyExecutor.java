package com.game.model.action.executor;

import com.game.model.action.ActionExecutor;
import com.game.model.minigame.MiniGame;

public class PushAllClosiestEnemyExecutor implements ActionExecutor {
    @Override
    public MiniGame apply(MiniGame miniGame) {

        //if()
        miniGame.pushAllCloserCreatures();

        return miniGame;

    }
}
