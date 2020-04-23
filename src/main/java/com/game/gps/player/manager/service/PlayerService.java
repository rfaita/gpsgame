package com.game.gps.player.manager.service;

import com.game.gps.player.manager.model.Player;
import com.game.gps.player.manager.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Mono<Player> findById(String id) {
        return playerRepository.findById(id);
    }

}
