package com.game.gps.player.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.gps.player.manager.dto.Message;

import static com.game.gps.player.manager.dto.MessageType.*;

import com.game.gps.player.manager.dto.PlayerPositionMessage;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.producer.PlayerPositionProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class DefaultWebSocketHandler implements WebSocketHandler {

    private final EmitterProcessor<Message> sendMessageEmitter;
    private final PlayerPositionProducer playerPositionProducer;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        return session.send(
                sendMessageEmitter
                        .filter(message -> "abc".equals(message.getPlayerId()))
                        .map(value -> {
                            try {
                                return mapper.writeValueAsString(value);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .map(session::textMessage)
        ).and(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .map(str -> {
                            try {
                                Message message = mapper.readValue(str, Message.class);
                                switch (message.getType()) {
                                    case TRACK_GPS:
                                        return mapper.readValue(str, PlayerPositionMessage.class);
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .map(message -> PlayerPosition.builder()
                                .id(message.getPlayerId())
                                .lat(message.getLat())
                                .lon(message.getLon())
                                .build())
                        .doOnNext(playerPositionProducer::send)
        );
    }
}
