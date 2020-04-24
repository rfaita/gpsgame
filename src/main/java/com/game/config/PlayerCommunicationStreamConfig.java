package com.game.config;

import com.game.dto.Message;
import com.game.service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class PlayerCommunicationStreamConfig {

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
