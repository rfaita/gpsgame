package com.game.config;

import com.game.dto.Message;
import com.game.dto.Position;
import com.game.dto.PositionMessage;
import com.game.model.PlayerPosition;
import com.game.service.PlayerPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class PlayerPositionStreamConfig {

    @Bean
    public EmitterProcessor<Message<Position>> playerGpsTrackerEmitter() {
        return EmitterProcessor.create();
    }

    @Bean
    public Supplier<Flux<Message<Position>>> playerGpsTrackerSupplier(
            final EmitterProcessor<Message<Position>> playerGpsTrackerEmitter) {
        return () -> playerGpsTrackerEmitter;
    }

    @Bean
    public Consumer<Flux<PositionMessage>> playerGpsTracker(final PlayerPositionService service) {

        return playerGpsTracker ->
                playerGpsTracker
                        .log(PlayerPositionStreamConfig.class.getName())
                        .map(positionMessage ->
                                PlayerPosition.builder()
                                        .id(positionMessage.getPlayerId())
                                        .lat(positionMessage.getPayload().getLat())
                                        .lon(positionMessage.getPayload().getLon())
                                        .build())
                        .flatMap(service::track)
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                        .onErrorContinue((throwable, o) -> log.error(throwable.getMessage(), throwable))
                        .subscribe();
    }
}
