package com.game.service;

import com.game.dto.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ReplayProcessor;

@Service
@Getter
@Slf4j
public class ReplyMessageService {

    private final ReplayProcessor<Message> processor;

    public ReplyMessageService() {
        this.processor = ReplayProcessor.cacheLast();
    }


}
