package com.game.producer;

import com.game.dto.ExecuteAction;
import com.game.dto.Message;
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
