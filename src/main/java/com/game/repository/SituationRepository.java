package com.game.repository;

import com.game.model.Situation;
import com.game.model.type.SituationType;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SituationRepository extends ReactiveMongoRepository<Situation, String> {

    @Aggregation(pipeline = {"{ $match: { usedInPlaces: { $all: ['?0']}, type: '?1' } }", "{ $sample: { size: 1 } }"})
    Mono<Situation> findByUsedInPlacesAndTypeAndRandom(String placeId, SituationType type);

}
