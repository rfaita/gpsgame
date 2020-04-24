package com.game.repository;

import com.game.model.Place;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends ReactiveMongoRepository<Place, String>, FindByRandomRepository<Place> {
}
