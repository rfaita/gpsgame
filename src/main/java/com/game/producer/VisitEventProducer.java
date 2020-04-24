package com.game.producer;

import com.game.dto.GenericValue;
import com.game.dto.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

@Component
@AllArgsConstructor
public class VisitEventProducer {

    private final EmitterProcessor<Message<GenericValue>> processor;

    public void send(Message<GenericValue> genericValueMessage) {
        this.processor.onNext(genericValueMessage);
    }
}
