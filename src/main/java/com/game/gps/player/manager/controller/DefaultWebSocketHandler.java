package com.game.gps.player.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.gps.player.manager.dto.Message;

import com.game.gps.player.manager.dto.PlayerPositionMessage;
import com.game.gps.player.manager.model.PlayerPosition;
import com.game.gps.player.manager.producer.PlayerPositionProducer;
import com.game.gps.player.manager.service.ReplyMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class DefaultWebSocketHandler implements WebSocketHandler {

    private final ReplyMessageService replyMessageService;
    private final PlayerPositionProducer playerPositionProducer;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private String getPlayerId(final WebSocketSession session) {
        UriTemplate template = new UriTemplate("/ws/game/{playerId}");
        Map<String, String> parameters = template.match(session.getHandshakeInfo().getUri().getPath());
        return parameters.getOrDefault("playerId", "undefined");
    }

    private String parseMessageToString(final Message message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing message error", e);
        }
    }

    private void handleMessage(final String playerId, final String message) {
        try {
            Message m = mapper.readValue(message, Message.class);
            switch (m.getType()) {
                case TRACK_GPS: {
                    PlayerPositionMessage playerPositionMessage = mapper.readValue(message, PlayerPositionMessage.class);
                    playerPositionProducer.send(PlayerPosition.builder()
                            .id(playerId)
                            .lat(playerPositionMessage.getLat())
                            .lon(playerPositionMessage.getLon())
                            .build());
                    break;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing message error", e);
        }
    }


    @Override
    public Mono<Void> handle(final WebSocketSession session) {

        return session.send(
                replyMessageService.getProcessor()
                        .filter(message -> getPlayerId(session).equals(message.getPlayerId()))
                        .map(DefaultWebSocketHandler.this::parseMessageToString)
                        .map(session::textMessage)
        ).and(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(message -> this.handleMessage(getPlayerId(session), message))
        );
    }
}
