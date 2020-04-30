package com.game.model.type;

public enum NoiseLevel {

    SILENT(0), LOW(1), HIGH(2), VERY_HIGH(3);

    private final Integer sound;

    public Integer getSound() {
        return sound;
    }

    NoiseLevel(Integer sound) {
        this.sound = sound;
    }
}
