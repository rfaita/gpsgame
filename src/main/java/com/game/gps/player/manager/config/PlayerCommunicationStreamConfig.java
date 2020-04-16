package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.service.ReplyMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class PlayerCommunicationStreamConfig {

    @Bean("playerCommunicationEmitter")
    public EmitterProcessor<Message> playerCommunicationEmitter() {
        return EmitterProcessor.create();
    }

    @Bean
    public Supplier<Flux<Message>> playerCommunicationSupplier(
            final EmitterProcessor<Message> playerCommunicationEmitter) {
        return () -> playerCommunicationEmitter;
    }
    @Bean
    public Consumer<Flux<Message>> playerCommunication(final ReplyMessageService replyMessageService) {

        return playerCommunication ->
                playerCommunication
                        .log(PlayerCommunicationStreamConfig.class.getName())
                        .doOnNext(replyMessageService.getProcessor()::onNext)
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                        .subscribe();
    }
}
