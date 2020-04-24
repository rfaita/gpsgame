package com.game.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import reactor.core.publisher.Mono;

public interface FindByRandomRepository<T> {

    @Aggregation("{ $sample: { size: 1 } }")
    Mono<T> findByRandom();
}
