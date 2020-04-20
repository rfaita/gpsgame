package com.game.gps.player.manager.config;


import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.dto.MessageType;
import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.dto.PositionMessage;
import com.game.gps.player.manager.model.Event;
import com.game.gps.player.manager.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Function;

@Configuration
@Slf4j
@AllArgsConstructor
public class EventGeneratorStreamConfig {

    @Bean
    public Function<Flux<PositionMessage>, Flux<Message<List<Event>>>> eventGenerator(final EventService service) {

        return eventGenerator ->
                eventGenerator
                        .log(EventGeneratorStreamConfig.class.getName())
                        .map(positionMessage ->
                                service.generate(
                                        Position.builder()
                                                .lat(positionMessage.getPayload().getLat())
                                                .lon(positionMessage.getPayload().getLon())
                                                .build())
                                        .map(eventId -> Message.<List<Event>>builder()
                                                .type(MessageType.NEW_EVENT_NOTIFICATION)
                                                .playerId(positionMessage.getPlayerId())
                                                .payload(List.of(
                                                        Event.builder()
                                                                .id(eventId)
                                                                .position(positionMessage.getPayload())
                                                                .build()
                                                        )
                                                )
                                                .build()
                                        )
                        )
                        .flatMap(message -> message)
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
