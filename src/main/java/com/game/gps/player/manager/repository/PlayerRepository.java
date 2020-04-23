package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.model.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends ReactiveMongoRepository<Player, String> {

}
