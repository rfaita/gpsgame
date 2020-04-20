package com.game.gps.player.manager.repository;

import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.model.Event;
import com.game.gps.player.manager.util.ListUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.game.gps.player.manager.util.ListUtil.concat;

@Repository
@AllArgsConstructor
public class EventRepository {

    private static final String KEY_EVENT_NAME = "event";
    private static final String KEY_EVENT_TIMEOUT_NAME = "event-timeout";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<Long> save(Event event) {

        Point point = new Point(event.getPosition().getLat(), event.getPosition().getLon());

        return this.redisTemplate.opsForZSet().add(KEY_EVENT_TIMEOUT_NAME, event.getId(), event.getExpireTime())
                .flatMap(l -> this.redisTemplate.opsForGeo().add(KEY_EVENT_NAME, point, event.getId()));
    }

    public Mono<Long> deleteById(String id) {
        return this.redisTemplate.opsForZSet().remove(KEY_EVENT_TIMEOUT_NAME, id)
                .flatMap(l -> this.redisTemplate.opsForGeo().remove(KEY_EVENT_NAME, id));
    }

    public Mono<Long> deleteByIds(List<String> ids) {
        return this.redisTemplate.opsForZSet().remove(KEY_EVENT_TIMEOUT_NAME, ids.toArray(new String[]{}))
                .flatMap(l -> this.redisTemplate.opsForGeo().remove(KEY_EVENT_NAME, ids.toArray(new String[]{})));
    }

    public Flux<String> findExpiredEvents() {

        Range<Double> range = Range.closed(0d, Double.valueOf(System.currentTimeMillis()));

        return this.redisTemplate.opsForZSet()
                .rangeByScore(KEY_EVENT_TIMEOUT_NAME, range);
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
