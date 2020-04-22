package com.game.gps.player.manager.service;

import com.game.gps.player.manager.model.Player;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class PlayerService {

    public Mono<Player> findById(String id) {
        return Mono.empty();
    }

}
