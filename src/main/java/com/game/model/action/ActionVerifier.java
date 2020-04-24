package com.game.model.action;

import com.game.model.minigame.MiniGame;

@FunctionalInterface
public interface ActionVerifier {

    Boolean apply(MiniGame miniGame);

}
