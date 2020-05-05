package com.game.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Rarity {

    private Integer rarity;
    private Double minRarity;
    private Double maxRarity;

    public static final Map<String, Rarity> calculateRarities(List<HasRarity> data) {
        final Integer maxRarity = data.stream()
                .map(obj -> obj.getRarity().getRarity())
                .reduce((integer, integer2) -> (integer > integer2) ? integer : integer2)
                .get();

        final Integer sumRarity = data.stream()
                .map(obj -> obj.getRarity().getRarity())
                .map(integer -> maxRarity - integer + 1)//inverse the value
                .reduce((integer, integer2) -> integer + integer2)
                .get();

        Map<String, Rarity> rarities = new HashMap<>();

        data.forEach(obj -> {
            Double value = (maxRarity - obj.getRarity().getRarity() + 1d) / sumRarity;

            Double lastValue = !rarities.isEmpty()
                    ? rarities.values().stream()
                    .map(rarity -> rarity.getMaxRarity())
                    .reduce((d, d2) -> (d > d2) ? d : d2)
                    .get()
                    : 0d;

            rarities.put(obj.getId(), Rarity.builder()
                    .rarity(obj.getRarity().getRarity())
                    .minRarity(lastValue)
                    .maxRarity(lastValue + value)
                    .build());

        });
        return rarities;
    }

}
