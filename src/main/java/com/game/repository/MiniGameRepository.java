package com.game.repository;

import com.game.model.minigame.MiniGame;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiniGameRepository extends ReactiveMongoRepository<MiniGame, String> {

}
