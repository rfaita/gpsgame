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

    public static Double random(Double maxValue) {
        return random(0d, maxValue);
    }

    public static Double randomDoublePercentage() {
        return random(0d, 100d);
    }

    public static Double random(Double minValue, Double maxValue) {
        return ThreadLocalRandom.current().nextDouble(maxValue);

    }
}
