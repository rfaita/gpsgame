package com.game.gps.player.manager.config;

import com.game.gps.player.manager.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.EmitterProcessor;

import java.util.Collections;

@Configuration
@Slf4j
public class WebSocketConfig {

    @Bean
    public EmitterProcessor<Message> sendMessageEmitter() {
        return EmitterProcessor.create();
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public HandlerMapping webSocketMapping(WebSocketHandler webSocketHandler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Collections.singletonMap("/ws/game", webSocketHandler));
        mapping.setCorsConfigurations(Collections.singletonMap("*", new CorsConfiguration().applyPermitDefaultValues()));
        mapping.setOrder(10);
        return mapping;
    }
}
