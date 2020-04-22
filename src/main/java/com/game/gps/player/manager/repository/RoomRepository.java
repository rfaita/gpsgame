package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.model.Room;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends ReactiveMongoRepository<Room, String> {

}
