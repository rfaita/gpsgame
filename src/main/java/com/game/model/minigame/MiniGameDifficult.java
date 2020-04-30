package com.game.model.minigame;

public enum MiniGameDifficult {

    EASY(1), MEDIUM(2), HARD(3), VERY_HARD(4);

    private final Integer difficult;

    public Integer getDifficult() {
        return difficult;
    }

    MiniGameDifficult(Integer difficult) {
        this.difficult = difficult;
    }
}
