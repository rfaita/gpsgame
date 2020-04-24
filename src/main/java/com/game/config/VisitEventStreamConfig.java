package com.game.config;

import com.game.dto.GenericValue;
import com.game.dto.Message;
import com.game.dto.MessageType;
import com.game.dto.VisitEventMessage;
import com.game.model.minigame.MiniGame;
import com.game.service.EventGeneratedService;
import com.game.service.MiniGameOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                                                        .build())
                                        .switchIfEmpty(
                                                Mono.just(Message.<MiniGame>builder()
                                                        .playerId(visitEventMessage.getPlayerId())
                                                        .type(MessageType.EVENT_NOT_FOUND)
                                                        .payload(MiniGame.builder().id(visitEventMessage.getPayload().getValue()).build())
                                                        .build())))
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
