package com.game.config;


import com.game.dto.Message;
import com.game.dto.MessageType;
import com.game.dto.Position;
import com.game.dto.PositionMessage;
import com.game.model.Event;
import com.game.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Function;

import static com.game.util.ListUtil.concat;

@Configuration
@Slf4j
@AllArgsConstructor
public class EventNotifierStreamConfig {

    @Bean
    public Function<Flux<PositionMessage>, Flux<Message<List<Event>>>> eventNotifier(final EventService service) {
        return eventGenerator ->
                eventGenerator
                        .log(EventNotifierStreamConfig.class.getName())
                        .flatMap(positionMessage ->
                                service.findAllByPosition(
                                        Position.builder()
                                                .lat(positionMessage.getPayload().getLat())
                                                .lon(positionMessage.getPayload().getLon())
                                                .build())
                                        .reduce(
                                                Message.<List<Event>>builder()
                                                        .type(MessageType.EVENT_NOTIFICATION)
                                                        .playerId(positionMessage.getPlayerId())
                                                        .payload(List.of())
                                                        .build(),
                                                (message, event) -> Message.<List<Event>>builder()
                                                        .type(message.getType())
                                                        .playerId(message.getPlayerId())
                                                        .payload(concat(message.getPayload(), event))
                                                        .build()
                                        ))
                        .filter(listMessage -> !listMessage.getPayload().isEmpty())
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
