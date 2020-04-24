package com.game.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomService {

    public Integer random(Integer maxValue) {
        return random(0, maxValue);
    }

    public Integer random(Integer minValue, Integer maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue + 1);

    }

    public Double random(Double maxValue) {
        return random(0d, maxValue);
    }

    public Double randomPercentage() {
        return random(0d, 100d);
    }

    public Double random(Double minValue, Double maxValue) {
        return ThreadLocalRandom.current().nextDouble(minValue, maxValue + 0.00000001d);

    }

}
