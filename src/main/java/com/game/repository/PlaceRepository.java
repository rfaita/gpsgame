package com.game.repository;

import com.game.model.Place;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlaceRepository extends ReactiveMongoRepository<Place, String>, FindByMinRarityLessThanEqualAndMaxRarityGreaterThan<Place> {

}
