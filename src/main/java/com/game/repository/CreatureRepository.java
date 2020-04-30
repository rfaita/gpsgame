package com.game.repository;

import com.game.model.Creature;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatureRepository extends ReactiveMongoRepository<Creature, String> {

}
