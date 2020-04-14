package com.game.gps.player.manager.producer;

import com.game.gps.player.manager.model.PlayerPosition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

@Component
@AllArgsConstructor
public class PlayerPositionProducer {

    private final EmitterProcessor<PlayerPosition> processor;

    public void send(PlayerPosition playerPosition) {
        this.processor.onNext(playerPosition);
    }
}
