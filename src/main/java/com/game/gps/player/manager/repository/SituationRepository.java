package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.model.Situation;
import com.game.gps.player.manager.model.type.SituationType;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface SituationRepository extends ReactiveMongoRepository<Situation, String>, FindByRandomRepository<Situation> {

    @Aggregation(pipeline = {"{ $match: { usedInPlaces: { $all: ['?0']}, type: '?1' } }", "{ $sample: { size: 1 } }"})
    Mono<Situation> findByUsedInPlacesAndTypeAndRandom(String placeId, SituationType type);

}
