package com.game.model.action.executor;

import com.game.model.Player;
import com.game.model.action.ActionExecutor;
import com.game.model.minigame.MiniGame;

public class AttackClosiestEnemyExecutor implements ActionExecutor {
    @Override
    public MiniGame apply(MiniGame miniGame) {

        Player player = miniGame.currentPlayerState();

        if (player.getId().equals("rfaita")) {
            Integer dano = 1000;
            miniGame.damageClosiestEnemy(dano);
        }

        return miniGame;
    }
}
