package com.game.repository;

import com.game.model.PlayerPosition;
import lombok.AllArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class PlayerPositionRepository {

    private static final String KEY_PLAYER_NAME = "player_position";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<Long> save(PlayerPosition playerPosition) {
        Point point = new Point(playerPosition.getLat(), playerPosition.getLon());
        return this.redisTemplate.opsForGeo().add(KEY_PLAYER_NAME, point, playerPosition.getId());
    }
}
