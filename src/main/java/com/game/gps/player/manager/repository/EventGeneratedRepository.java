package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.model.Situation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventGeneratedRepository extends ReactiveMongoRepository<EventGenerated, String> {


}
