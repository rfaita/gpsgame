package com.game.config;

import com.game.dto.ExecuteAction;
import com.game.dto.ExecuteActionMessage;
import com.game.dto.Message;
import com.game.dto.MessageType;
import com.game.model.minigame.representation.MiniGameRepresentation;
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
public class ExecuteActionStreamConfig {

    @Bean
    public EmitterProcessor<Message<ExecuteAction>> executeActionEmitter() {
        return EmitterProcessor.create();
    }

    @Bean
    public Supplier<Flux<Message<ExecuteAction>>> executeActionSupplier(
            final EmitterProcessor<Message<ExecuteAction>> executeActionEmitter) {
        return () -> executeActionEmitter;
    }

    @Bean
    public Function<Flux<ExecuteActionMessage>, Flux<Message<MiniGameRepresentation>>> executeAction(final MiniGameOrchestrator service) {

        return executeAction ->
                executeAction
                        .log(ExecuteActionStreamConfig.class.getName())
                        .flatMap(executeActionMessage ->
                                service.executeAction(
                                        executeActionMessage.getPlayerId(),
                                        executeActionMessage.getPayload().getMiniGameId(),
                                        executeActionMessage.getPayload().getActionId())
                                        .map(miniGame ->
                                                Message.<MiniGameRepresentation>builder()
                                                        .playerId(executeActionMessage.getPlayerId())
                                                        .type(MessageType.UPDATE_EVENT)
                                                        .payload(miniGame)
                                                        .build())
                                        .onErrorResume(throwable ->
                                                this.defaultResponse(throwable, executeActionMessage.getPlayerId()))
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
