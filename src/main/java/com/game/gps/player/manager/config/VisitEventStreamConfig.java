package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.GenericValue;
import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.dto.MessageType;
import com.game.gps.player.manager.dto.VisitEventMessage;
import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.model.minigame.MiniGame;
import com.game.gps.player.manager.service.EventGeneratedService;
import com.game.gps.player.manager.service.MiniGameOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

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
    public Function<Flux<VisitEventMessage>, Flux<Message<MiniGame>>> visitEvent(final EventGeneratedService service,
                                                                                 final MiniGameOrchestrator miniGameOrchestrator) {

        return visitEvent ->
                visitEvent
                        .log(VisitEventStreamConfig.class.getName())
                        .flatMap(visitEventMessage ->
                                service.visit(visitEventMessage.getPayload().getValue())
                                        .flatMap(eventGenerated -> miniGameOrchestrator.start(visitEventMessage.getPlayerId(), eventGenerated))
                                        .map(miniGame ->
                                                Message.<MiniGame>builder()
                                                        .playerId(visitEventMessage.getPlayerId())
                                                        .type(MessageType.START_EVENT)
                                                        .payload(miniGame)
                                                        .build()))
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
