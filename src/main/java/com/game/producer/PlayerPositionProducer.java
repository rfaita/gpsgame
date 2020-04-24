package com.game.producer;

import com.game.dto.Message;
import com.game.dto.Position;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

@Component
@AllArgsConstructor
public class PlayerPositionProducer {

    private final EmitterProcessor<Message<Position>> processor;

    public void send(Message<Position> playerPosition) {
        this.processor.onNext(playerPosition);
    }
}
