package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.*;
import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.model.minigame.MiniGame;
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
    public Function<Flux<ExecuteActionMessage>, Flux<Message<MiniGame>>> executeAction(final MiniGameOrchestrator service) {

        return executeAction ->
                executeAction
                        .log(ExecuteActionStreamConfig.class.getName())
                        .flatMap(executeActionMessage ->
                                service.executeAction(executeActionMessage.getPayload().getMiniGameId(), executeActionMessage.getPayload().getActionId())
                                        .map(miniGame ->
                                                Message.<MiniGame>builder()
                                                        .playerId(executeActionMessage.getPlayerId())
                                                        .type(MessageType.UPDATE_EVENT)
                                                        .payload(miniGame)
                                                        .build()))
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
