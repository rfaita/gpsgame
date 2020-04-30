package com.game.config;

import com.game.dto.GenericValue;
import com.game.dto.Message;
import com.game.dto.MessageType;
import com.game.dto.VisitEventMessage;
import com.game.model.minigame.MiniGame;
import com.game.model.minigame.representation.MiniGameRepresentation;
import com.game.service.EventGeneratedService;
import com.game.service.MiniGameOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    public Function<Flux<VisitEventMessage>, Flux<Message<MiniGameRepresentation>>> visitEvent(final EventGeneratedService service,
                                                                                               final MiniGameOrchestrator miniGameOrchestrator) {

        return visitEvent ->
                visitEvent
                        .log(VisitEventStreamConfig.class.getName())
                        .flatMap(visitEventMessage ->
                                service.visit(visitEventMessage.getPayload().getValue())
                                        .flatMap(eventGenerated -> miniGameOrchestrator.start(visitEventMessage.getPlayerId(), eventGenerated))
                                        .map(miniGame ->
                                                Message.<MiniGameRepresentation>builder()
                                                        .playerId(visitEventMessage.getPlayerId())
                                                        .type(MessageType.START_EVENT)
                                                        .payload(miniGame)
                                                        .build())
                                        .switchIfEmpty(
                                                Mono.just(Message.<MiniGameRepresentation>builder()
                                                        .playerId(visitEventMessage.getPlayerId())
                                                        .type(MessageType.EVENT_NOT_FOUND)
                                                        .payload(MiniGameRepresentation.builder().id(visitEventMessage.getPayload().getValue()).build())
                                                        .build()))
                                        .onErrorResume(throwable ->
                                                this.defaultResponse(throwable, visitEventMessage.getPlayerId()))
                        );
    }

    private Mono<Message<MiniGameRepresentation>> defaultResponse(Throwable throwable, String playerId) {
        log.error(throwable.getMessage(), throwable);
        return Mono.just(
                Message.<MiniGameRepresentation>builder()
                        .playerId(playerId)
                        .type(MessageType.ERROR)
                        .payload(MiniGameRepresentation.builder().id(MDC.get("X-B3-TraceId")).build())
                        .build());
    }
}
