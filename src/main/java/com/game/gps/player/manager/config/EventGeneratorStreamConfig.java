package com.game.gps.player.manager.config;


import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@Slf4j
@AllArgsConstructor
public class EventGeneratorStreamConfig {


    @Bean
    public Consumer<Flux<PlayerPosition>> eventGenerator(final EventService service) {

        return eventGenerator ->
                eventGenerator
                        .map(playerPosition -> Position.builder().lat(playerPosition.getLat()).lon(playerPosition.getLon()).build())
                        .flatMap(service::generate)
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                        .subscribe();
    }
}
