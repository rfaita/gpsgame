package com.game.gps.player.manager.producer;

import com.game.gps.player.manager.dto.GenericValue;
import com.game.gps.player.manager.dto.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

@Component
@AllArgsConstructor
public class VisitEventProducer {

    private final EmitterProcessor<Message<GenericValue>> processor;

    public void send(Message<GenericValue> playerPosition) {
        this.processor.onNext(playerPosition);
    }
}
