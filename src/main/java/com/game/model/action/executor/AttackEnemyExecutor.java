package com.game.model.action.executor;

import com.game.model.Player;
import com.game.model.action.ActionExecutor;
import com.game.model.minigame.MiniGame;

public class AttackEnemyExecutor implements ActionExecutor {
    @Override
    public MiniGame apply(MiniGame miniGame) {

        Player player = miniGame.getCurrentPlayerState();

        //calculate the percentage of miss or not
        if (true) {
            Integer dano = 10; //player.getMainWeapon().getDmg()
            miniGame.damageCloserEnemy(dano);
        }

        return miniGame;
    }
}
