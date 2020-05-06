package com.game.model.action.executor;

import com.game.model.Player;
import com.game.model.action.ActionExecutor;
import com.game.model.minigame.MiniGame;

public class AttackPlayerExecutor implements ActionExecutor {
    @Override
    public MiniGame apply(MiniGame miniGame) {

        Player player = miniGame.getCurrentPlayerState();

        if (player.getId().equals("rfaita")) {
            Integer dano = 1000;
            miniGame.damagePlayer(dano);
        }

        return miniGame;
    }
}
