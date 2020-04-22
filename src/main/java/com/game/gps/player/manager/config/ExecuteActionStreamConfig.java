package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.GenericValue;
import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.dto.MessageType;
import com.game.gps.player.manager.dto.VisitEventMessage;
import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.service.EventGeneratedService;
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
    public EmitterProcessor<Message<GenericValue>> executeAction() {
        return EmitterProcessor.create();
    }

    @Bean
    public Supplier<Flux<Message<GenericValue>>> executeActionSupplier(
            final EmitterProcessor<Message<GenericValue>> executeActionEmitter) {
        return () -> executeActionEmitter;
    }

    @Bean
    public Function<Flux<VisitEventMessage>, Flux<Message<EventGenerated>>> executeAction(final EventGeneratedService service) {

        return visitEvent ->
                visitEvent
                        .log(ExecuteActionStreamConfig.class.getName())
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
