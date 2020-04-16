package com.game.gps.player.manager.config;


import com.game.gps.player.manager.dto.EventMessage;
import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.dto.MessageType;
import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.service.EventService;
import com.game.gps.player.manager.service.ReplyMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@Slf4j
@AllArgsConstructor
public class EventNotifierStreamConfig {

    @Bean
    public Consumer<Flux<PlayerPosition>> eventNotifier(final EventService service,
                                                        final EmitterProcessor<Message> playerCommunicationEmitter) {
        return eventGenerator ->
                eventGenerator
                        .log(EventNotifierStreamConfig.class.getName())
                        .flatMap(playerPosition ->
                                service.findAllByPosition(
                                        Position.builder()
                                                .lat(playerPosition.getLat())
                                                .lon(playerPosition.getLon())
                                                .build())
                                        .map(event ->
                                                EventMessage.builder()
                                                        .type(MessageType.EVENT_NOTIFICATION)
                                                        .playerId(playerPosition.getId())
                                                        .event(event)
                                                        .build()))
                        .doOnNext(playerCommunicationEmitter::onNext)
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                        .subscribe();
    }
}
