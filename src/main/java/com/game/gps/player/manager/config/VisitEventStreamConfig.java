package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.*;
import com.game.gps.player.manager.model.Event;
import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.service.EventGeneratedService;
import com.game.gps.player.manager.service.PlayerPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class VisitEventStreamConfig {

    @Bean
    public EmitterProcessor<Message<GenericValue>> visitEventEmitter() {
        return EmitterProcessor.create();
    }

    @Bean
    public Supplier<Flux<Message<GenericValue>>> visitEventSupplier(
            final EmitterProcessor<Message<GenericValue>> visitEventEmitter) {
        return () -> visitEventEmitter;
    }

    @Bean
    public Function<Flux<VisitEventMessage>, Flux<Message<EventGenerated>>> visitEvent(final EventGeneratedService service) {

        return visitEvent ->
                visitEvent
                        .log(VisitEventStreamConfig.class.getName())
                        .flatMap(visitEventMessage ->
                                service.visit(visitEventMessage.getPayload().getValue())
                                .map(eventGenerated ->
                                        Message.<EventGenerated>builder()
                                                .playerId(visitEventMessage.getPlayerId())
                                                .type(MessageType.START_EVENT)
                                                .payload(eventGenerated)
                                                .build()))
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
