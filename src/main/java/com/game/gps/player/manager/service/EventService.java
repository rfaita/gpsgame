package com.game.gps.player.manager.service;

import com.game.gps.player.manager.config.properties.EventProperties;
import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.model.Event;
import com.game.gps.player.manager.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EventService {

    private final EventProperties eventProperties;
    private final RandomService randomService;
    private final EventRepository eventRepository;

    public Mono<String> generate(Position position) {
        if (randomService.randomPercentage() <= eventProperties.getEventPercentageGenerator()) {

            final String id = UUID.randomUUID().toString();

            return this.canGenerateEventsInRadius(position)
                    .filter(Boolean::booleanValue)
                    .map(canDo -> Event.builder()
                            .id(id)
                            .position(position)
                            .build())
                    .flatMap(eventRepository::save)
                    .map(l -> id);
        }
        return Mono.empty();

    }

    private Mono<Boolean> canGenerateEventsInRadius(Position position) {

        return eventRepository.findByCircle(position, eventProperties.getRadiusToFindEvents())
                .count()
                .map(count -> count < eventProperties.getMaxEventsInArea());

    }

    public Flux<Event> findAllByPosition(Position position) {

        return eventRepository.findByCircle(position, eventProperties.getRadiusToFindEvents());

    }
}
