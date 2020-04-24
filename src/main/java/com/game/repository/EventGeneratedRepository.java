package com.game.repository;

import com.game.model.EventGenerated;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventGeneratedRepository extends ReactiveMongoRepository<EventGenerated, String> {


}
