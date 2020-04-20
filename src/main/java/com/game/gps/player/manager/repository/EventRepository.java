package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.model.Event;
import lombok.AllArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class EventRepository {

    private static final String KEY_EVENT_NAME = "event";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<Long> save(Event event) {
        Point point = new Point(event.getPosition().getLat(), event.getPosition().getLon());
        return this.redisTemplate.opsForGeo().add(KEY_EVENT_NAME, point, event.getId());
    }

    public Flux<Event> findByCircle(Position position, Integer radius) {
        Circle circle = new Circle(position.getLat(), position.getLon(), radius);
        RedisGeoCommands.GeoRadiusCommandArgs commandArgs
                = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates();
        return this.redisTemplate.opsForGeo().radius(KEY_EVENT_NAME, circle, commandArgs)
                .map(GeoResult::getContent)
                .map(stringGeoLocation ->
                        Event.builder()
                                .id(stringGeoLocation.getName())
                                .position(Position.builder()
                                        .lat(stringGeoLocation.getPoint().getX())
                                        .lon(stringGeoLocation.getPoint().getY())
                                        .build())
                                .build());


    }

}
