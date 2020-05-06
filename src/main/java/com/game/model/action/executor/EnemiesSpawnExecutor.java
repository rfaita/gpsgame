package com.game.model.action.executor;

import com.game.model.Player;
import com.game.model.action.ActionExecutor;
import com.game.model.minigame.MiniGame;
import com.game.model.type.NoiseLevel;
import com.game.util.RandomUtil;

public class EnemiesSpawnExecutor implements ActionExecutor {
    @Override
    public MiniGame apply(MiniGame miniGame) {

        Player player = miniGame.getCurrentPlayerState();

        NoiseLevel weaponNoiseLevel = NoiseLevel.LOW;//player.getMainWeapon().getNoiseLevel();

        if (RandomUtil.random(NoiseLevel.VERY_HIGH.getSound() + 1) < weaponNoiseLevel.getSound()) {
            miniGame.spawnEnemy();
        }

        return miniGame;
    }
}
