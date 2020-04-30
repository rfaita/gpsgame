package com.game.service;

import com.game.model.PlayerPosition;
import com.game.repository.PlayerPositionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class PlayerPositionService {

    private final PlayerPositionRepository repository;

    public Mono<Long> track(PlayerPosition playerPosition) {
        log.info("Saving: {}", playerPosition);
        return this.repository.save(playerPosition);
    }


}
