package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.dto.PositionMessage;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.service.PlayerPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.BiConsumer;
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
                        .log(EmitterProcessor.class.getName())
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
