package com.game.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {


    public static Integer random(Integer maxValue) {
        return random(0, maxValue);
    }

    public static Integer randomPercentage() {
        return random(0, 100);
    }

    public static Integer random(Integer minValue, Integer maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue + 1);
    }
}
