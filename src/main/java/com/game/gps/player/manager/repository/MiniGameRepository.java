package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.model.minigame.MiniGame;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiniGameRepository extends ReactiveMongoRepository<MiniGame, String> {

}
