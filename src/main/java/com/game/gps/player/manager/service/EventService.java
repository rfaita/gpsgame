package com.game.gps.player.manager.service;

import com.game.gps.player.manager.config.properties.EventProperties;
import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.model.Event;
import com.game.gps.player.manager.model.Place;
import com.game.gps.player.manager.repository.EventRepository;
import com.game.gps.player.manager.repository.PlaceRepository;
import com.game.gps.player.manager.repository.SituationRepository;
import com.mongodb.client.model.Aggregates;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private final EventProperties eventProperties;
    private final RandomService randomService;
    private final EventRepository eventRepository;

    public Mono<String> generate(Position position) {

        final String id = UUID.randomUUID().toString();

        return this.rollDice()
                .filter(Boolean::booleanValue)
                .flatMap(canDo -> this.canGenerateEventsInRadius(position))
                .filter(Boolean::booleanValue)
                .map(canDo -> Event.builder()
                        .id(id)
                        .position(position)
                        .expireTime(generateEventDuration())
                        .build())
                .flatMap(eventRepository::save)
                .map(l -> id);

    }

    private Long generateEventDuration() {
        return System.currentTimeMillis()
                + randomService.random(eventProperties.getMinEventDuration(), eventProperties.getMaxEventDuration());
    }

    private Mono<Boolean> rollDice() {
        return Mono.just(randomService.randomPercentage() <= eventProperties.getEventPercentageGenerator());
    }

    private Mono<Boolean> canGenerateEventsInRadius(Position position) {

        return eventRepository.findByCircle(position, eventProperties.getRadiusToFindEvents())
                .count()
                .map(count -> count < eventProperties.getMaxEventsInArea());

    }

    public Flux<Event> findAllByPosition(Position position) {

        return eventRepository.findByCircle(position, eventProperties.getRadiusToFindEvents());

    }

    @Scheduled(fixedDelay = 10000)
    public void expire() {

        log.info("Expire events started.");
        this.eventRepository.findExpiredEvents()
                .reduce(new ArrayList<String>(), (list, id) -> {
                    list.add(id);
                    return list;
                })
                .filter(ids -> !ids.isEmpty())
                .flatMap(this.eventRepository::deleteByIds)
                .subscribe(size -> log.info("Events expired: {}", size));
    }

}
