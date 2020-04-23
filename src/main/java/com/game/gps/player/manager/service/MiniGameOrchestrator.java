package com.game.gps.player.manager.service;

import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.model.minigame.MiniGame;
import com.game.gps.player.manager.model.minigame.MiniGameDataCache;
import com.game.gps.player.manager.repository.ActionRepository;
import com.game.gps.player.manager.repository.MiniGameRepository;
import com.game.gps.player.manager.repository.RoomRepository;
import com.game.gps.player.manager.repository.SituationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MiniGameOrchestrator {

    private final EventGeneratedService eventGeneratedService;

    private final ActionRepository actionRepository;
    private final SituationRepository situationRepository;
    private final RoomRepository roomRepository;

    private final PlayerService playerService;

    private final MiniGameRepository miniGameRepository;

    public Mono<MiniGame> start(String playerId, EventGenerated event) {
        return start(playerId,  Mono.just(event));
    }

    public Mono<MiniGame> start(String playerId, String eventGeneratedId) {
        return start(playerId, this.eventGeneratedService.findById(eventGeneratedId));
    }

    public Mono<MiniGame> start(String playerId, Mono<EventGenerated> eventGeneratedMono) {

        return Mono.just(MiniGame.builder().id(UUID.randomUUID().toString()))
                .zipWith(eventGeneratedMono,
                        (miniGameBuilder, eventGenerated) -> miniGameBuilder.eventGenerated(eventGenerated))
                .zipWith(this.loadMiniGameDataCache(),
                        (miniGameBuilder, miniGameDataCache) -> miniGameBuilder.dataCache(miniGameDataCache))
                .map(miniGameBuilder -> miniGameBuilder.build())
                .flatMap(miniGame -> this.playerService.findById(playerId)
                        .map(player -> miniGame.start(player)))
                .flatMap(this::saveMiniGameState);
    }

    public Mono<MiniGame> executeAction(String miniGameId, String actionId) {
        return this.loadMiniGameState(miniGameId)
                .flatMap(miniGame -> miniGame.executeAction(actionId))
                .flatMap(this::saveMiniGameState);
    }

    public void finish(String miniGameId) {
        //this.playerService.save()
    }

    private Mono<MiniGameDataCache> loadMiniGameDataCache() {
        return Mono.just(MiniGameDataCache.builder())
                .zipWith(this.roomRepository.findAll().collectList(),
                        (miniGameDataCacheBuilder, rooms) -> miniGameDataCacheBuilder.allRooms(rooms))
                .zipWith(this.situationRepository.findAll().collectList(),
                        (miniGameDataCacheBuilder, situations) -> miniGameDataCacheBuilder.allSituations(situations))
                .zipWith(this.actionRepository.findAll().collectList(),
                        (miniGameDataCacheBuilder, actions) -> miniGameDataCacheBuilder.allActions(actions))
                .map(miniGameDataCacheBuilder -> miniGameDataCacheBuilder.build());
    }

    private Mono<MiniGame> loadMiniGameState(String miniGameId) {
        return this.miniGameRepository.findById(miniGameId)
                .flatMap(miniGame -> this.loadMiniGameDataCache()
                        .map(miniGameDataCache -> miniGame.dataCache(miniGameDataCache))
                );

    }


    private Mono<MiniGame> saveMiniGameState(MiniGame miniGame) {
        return Mono.just(miniGame)
                .map(MiniGame::clearDataCache)
                .flatMap(this.miniGameRepository::save);
    }

}
