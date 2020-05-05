package com.game.model.action.verifier;

import com.game.model.action.ActionVerifier;
import com.game.model.minigame.MiniGame;

public class HasAmmoRangedWeaponVerifier implements ActionVerifier {
    @Override
    public Boolean apply(MiniGame miniGame) {
        return Boolean.TRUE;
    }
}
