package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.model.Place;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends ReactiveMongoRepository<Place, String>, FindByRandomRepository<Place> {
}
