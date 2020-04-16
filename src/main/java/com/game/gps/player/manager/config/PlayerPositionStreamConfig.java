package com.game.gps.player.manager.config;

import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.service.PlayerPositionService;
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
    public EmitterProcessor<PlayerPosition> playerGpsTrackerEmitter() {
        return EmitterProcessor.create();
    }

    @Bean
    public Supplier<Flux<PlayerPosition>> playerGpsTrackerSupplier(
            final EmitterProcessor<PlayerPosition> playerGpsTrackerEmitter) {
        return () -> playerGpsTrackerEmitter;
    }

    @Bean
    public Consumer<Flux<PlayerPosition>> playerGpsTracker(final PlayerPositionService service) {

        return playerGpsTracker ->
                playerGpsTracker
                        .log(EmitterProcessor.class.getName())
                        .flatMap(service::track)
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                        .subscribe();
    }
}
