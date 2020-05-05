package com.game.repository;

import reactor.core.publisher.Mono;

public interface FindByMinRarityLessThanEqualAndMaxRarityGreaterThan<T> {

    Mono<T> findByRarity_MinRarityLessThanEqualAndRarity_MaxRarityGreaterThan(Double minRarity, Double maxRarity);
}
