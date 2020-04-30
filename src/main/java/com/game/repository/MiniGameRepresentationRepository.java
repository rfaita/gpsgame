package com.game.repository;

import com.game.model.minigame.representation.MiniGameRepresentation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiniGameRepresentationRepository extends ReactiveMongoRepository<MiniGameRepresentation, String> {

}
