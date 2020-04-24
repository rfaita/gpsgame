package com.game.service;

import com.game.model.Player;
import com.game.repository.PlayerRepository;
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
