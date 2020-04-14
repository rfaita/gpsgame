package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.service.PlayerPositionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@Slf4j
@AllArgsConstructor
public class PlayerCommunicationStreamConfig {

    private final EmitterProcessor<Message> sendMessageEmitter;

    @Bean
    public Consumer<Flux<Message>> playerCommunication() {

        return playerCommunication ->
                playerCommunication
                        .doOnNext(sendMessageEmitter::onNext)
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                        .subscribe();
    }
}
