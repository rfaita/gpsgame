package com.game.gps.player.manager.service;

import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.model.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class EventService {

    public Mono<Event> generate(Position position) {

        return Mono.just(Event.builder().id(UUID.randomUUID().toString()).name("teste").build());

    }

    public Flux<Event> findAllByPosition(Position position) {

        return Flux.just(Event.builder().id(UUID.randomUUID().toString()).name("teste").build(),
                Event.builder().id(UUID.randomUUID().toString()).name("teste").build(),
                Event.builder().id(UUID.randomUUID().toString()).name("teste").build(),
                Event.builder().id(UUID.randomUUID().toString()).name("teste").build());


    }
}
