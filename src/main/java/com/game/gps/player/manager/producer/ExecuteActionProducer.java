package com.game.gps.player.manager.producer;

import com.game.gps.player.manager.dto.ExecuteAction;
import com.game.gps.player.manager.dto.GenericValue;
import com.game.gps.player.manager.dto.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

@Component
@AllArgsConstructor
public class ExecuteActionProducer {

    private final EmitterProcessor<Message<ExecuteAction>> processor;

    public void send(Message<ExecuteAction> executeActionMessage) {
        this.processor.onNext(executeActionMessage);
    }
}
