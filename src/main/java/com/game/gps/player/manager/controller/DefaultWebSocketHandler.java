package com.game.gps.player.manager.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.gps.player.manager.dto.ExecuteAction;
import com.game.gps.player.manager.dto.GenericValue;
import com.game.gps.player.manager.dto.Message;
import com.game.gps.player.manager.dto.Position;
import com.game.gps.player.manager.producer.ExecuteActionProducer;
import com.game.gps.player.manager.producer.PlayerPositionProducer;
import com.game.gps.player.manager.producer.VisitEventProducer;
import com.game.gps.player.manager.service.ReplyMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class DefaultWebSocketHandler implements WebSocketHandler {

    private final ReplyMessageService replyMessageService;
    private final PlayerPositionProducer playerPositionProducer;
    private final VisitEventProducer visitEventProducer;
    private final ExecuteActionProducer executeActionProducer;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private String getPlayerId(final WebSocketSession session) {
        UriTemplate template = new UriTemplate("/ws/game/{playerId}");
        Map<String, String> parameters = template.match(session.getHandshakeInfo().getUri().getPath());
        return parameters.getOrDefault("playerId", "undefined");
    }

    private String getPlayerIdFromMessage(final String message) {
        try {
            Message m = mapper.readValue(message, Message.class);
            return m.getPlayerId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing message error", e);
        }
    }

    private String parseMessageToString(final Message message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing message error", e);
        }
    }

    private Flux<Void> handleMessage(final String playerId, final String message) {
        try {
            Message m = mapper.readValue(message, Message.class);
            switch (m.getType()) {
                case TRACK_GPS: {
                    Message<Position> fullMessage = mapper.readValue(message, new TypeReference<>() {
                    });
                    fullMessage.setPlayerId(playerId);
                    playerPositionProducer.send(fullMessage);
                    break;
                }
                case VISIT_EVENT: {
                    Message<GenericValue> fullMessage = mapper.readValue(message, new TypeReference<>() {
                    });
                    fullMessage.setPlayerId(playerId);
                    visitEventProducer.send(fullMessage);
                    break;
                }
                case EXECUTE_ACTION_EVENT: {
                    Message<ExecuteAction> fullMessage = mapper.readValue(message, new TypeReference<>() {
                    });
                    fullMessage.setPlayerId(playerId);
                    executeActionProducer.send(fullMessage);
                    break;
                }
            }

            return Flux.never();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing message error", e);
        }
    }


    @Override
    public Mono<Void> handle(final WebSocketSession session) {

        return session.send(
                replyMessageService.getProcessor()
                        .filter(message -> getPlayerId(session).equals(message.getPlayerId()))
                        .log(DefaultWebSocketHandler.class.getName() + ".out")
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                        .map(this::parseMessageToString)
                        .map(session::textMessage)
        ).and(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log(DefaultWebSocketHandler.class.getName() + ".in")
                        .doOnNext(message -> this.handleMessage(getPlayerId(session), message))
                        .onErrorContinue((throwable, o) -> log.error(throwable.getMessage(), throwable))
                        .doOnTerminate(() -> log.info("Client exit."))
                        .doOnSubscribe(subscription -> log.info("New Client connect: {}", subscription))
                        .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
        );
    }
}
