package com.game.gps.player.manager.model.action;

import com.game.gps.player.manager.model.minigame.MiniGame;

@FunctionalInterface
public interface ActionVerifier {

    Boolean apply(MiniGame miniGame);
}
