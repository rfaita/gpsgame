package com.game.service;

import com.game.exception.GenericException;
import com.game.model.EventGenerated;
import com.game.model.minigame.MiniGame;
import com.game.repository.MiniGameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.game.exception.ExceptionMessageConstant.MINI_GAME_NOT_FOUND;
import static com.game.exception.ExceptionMessageConstant.PLAYER_ACCESS_NOT_PERMITTED_GAME;

@Service
@AllArgsConstructor
public class MiniGameOrchestrator {

    private final EventGeneratedService eventGeneratedService;
    private final MiniGameDataCacheService miniGameDataCacheService;

    private final PlayerService playerService;

    private final MiniGameRepository miniGameRepository;

    public Mono<MiniGame> start(String playerId, EventGenerated event) {
        return start(playerId, Mono.just(event));
    }

    public Mono<MiniGame> start(String playerId, String eventGeneratedId) {
        return start(playerId, this.eventGeneratedService.findById(eventGeneratedId));
    }

    public Mono<MiniGame> start(String playerId, Mono<EventGenerated> eventGeneratedMono) {

        return Mono.just(MiniGame.builder().id(UUID.randomUUID().toString()))
                .zipWith(eventGeneratedMono,
                        (miniGameBuilder, eventGenerated) -> miniGameBuilder.eventGenerated(eventGenerated))
                .zipWith(this.miniGameDataCacheService.load(),
                        (miniGameBuilder, miniGameDataCache) -> miniGameBuilder.dataCache(miniGameDataCache))
                .map(miniGameBuilder -> miniGameBuilder.build())
                .flatMap(miniGame -> this.playerService.findById(playerId)
                        .map(player -> miniGame.start(player)))
                .flatMap(this::saveMiniGameState);
    }

    public Mono<MiniGame> executeAction(String playerId, String miniGameId, String actionId) {
        return this.loadMiniGameState(miniGameId)
                .filter(miniGame -> miniGame.getPlayerId().equals(playerId))
                .switchIfEmpty(Mono.error(() -> GenericException.of(PLAYER_ACCESS_NOT_PERMITTED_GAME, playerId, miniGameId)))
                .map(miniGame -> miniGame.executeAction(actionId))
                .flatMap(this::saveMiniGameState);
    }

    public void finish(String miniGameId) {
        //this.playerService.save()
    }

    private Mono<MiniGame> loadMiniGameState(String miniGameId) {
        return this.miniGameRepository.findById(miniGameId)
                .switchIfEmpty(Mono.error(GenericException.of(MINI_GAME_NOT_FOUND, miniGameId)))
                .flatMap(miniGame -> this.miniGameDataCacheService.load()
                        .map(miniGameDataCache -> miniGame.dataCache(miniGameDataCache))
                );

    }


    private Mono<MiniGame> saveMiniGameState(MiniGame miniGame) {
        return Mono.just(miniGame)
                .map(MiniGame::clearDataCache)
                .flatMap(this.miniGameRepository::save)
                .map(MiniGame::clearStateHistory);
    }

}
