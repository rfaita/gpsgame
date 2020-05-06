package com.game.service;

import com.game.config.properties.GameProperties;
import com.game.dto.Position;
import com.game.exception.GenericException;
import com.game.model.EventGenerated;
import com.game.model.minigame.MiniGame;
import com.game.model.minigame.Stage;
import com.game.model.minigame.representation.MiniGameRepresentation;
import com.game.repository.MiniGameRepresentationRepository;
import com.game.util.GeoUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.game.exception.ExceptionMessageConstant.*;

@Service
@AllArgsConstructor
public class MiniGameOrchestrator {

    private final EventGeneratedService eventGeneratedService;
    private final MiniGameDataCacheService miniGameDataCacheService;

    private final PlayerService playerService;
    private final PlayerPositionService playerPositionService;

    private final MiniGameRepresentationRepository miniGameRepository;

    private final GameProperties eventProperties;

    public Mono<MiniGameRepresentation> start(String playerId, EventGenerated event) {
        return start(playerId, Mono.just(event));
    }

    public Mono<MiniGameRepresentation> start(String playerId, String eventGeneratedId) {
        return start(playerId, this.eventGeneratedService.findById(eventGeneratedId));
    }

    public Mono<MiniGameRepresentation> start(String playerId, Mono<EventGenerated> eventGeneratedMono) {

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

    public Mono<MiniGameRepresentation> executeAction(String playerId, String miniGameId, String actionId) {
        return this.loadMiniGameState(miniGameId)
                .filter(miniGame -> miniGame.getPlayerId().equals(playerId))
                .switchIfEmpty(Mono.error(() -> GenericException.of(PLAYER_ACCESS_NOT_PERMITTED_GAME, playerId, miniGameId)))
                .zipWith(playerPositionService.findByPlayerId(playerId))
                .filter(tuple -> isPlayerInMiniGameRadius(tuple.getT1(), tuple.getT2().toPosition()))
                .switchIfEmpty(Mono.error(() -> GenericException.of(PLAYER_ACCESS_NOT_IN_RADIUS_OF_GAME, playerId, miniGameId)))
                .map(tuple -> tuple.getT1())
                .map(miniGame -> miniGame.executeAction(actionId))
                .flatMap(this::saveMiniGameState);
    }

    private Boolean isPlayerInMiniGameRadius(MiniGame miniGame, Position playerPosition) {

        return GeoUtil.distance(miniGame.getEventGenerated().getPosition(), playerPosition)
                <= eventProperties.getRadiusToIteractWithEvent();
    }


    private Mono<MiniGame> loadMiniGameState(String miniGameId) {
        return this.miniGameRepository.findById(miniGameId)
                .switchIfEmpty(Mono.error(GenericException.of(MINI_GAME_NOT_FOUND, miniGameId)))
                .filter(miniGameRepresentation -> miniGameRepresentation.getStage().equals(Stage.RUNNING))
                .switchIfEmpty(Mono.error(GenericException.of(MINI_GAME_ENDED, miniGameId)))
                .map(MiniGameRepresentation::toMiniGame)
                .flatMap(miniGame -> this.miniGameDataCacheService.load()
                        .map(miniGameDataCache -> miniGame.dataCache(miniGameDataCache))
                )
                .map(miniGame -> miniGame.loadObservers());

    }

    private Mono<MiniGameRepresentation> saveMiniGameState(MiniGame miniGame) {
        return Mono.just(miniGame)
                .map(MiniGame::toMiniGameRepresentation)
                .flatMap(this.miniGameRepository::save)
                .map(MiniGameRepresentation::clearStateHistory);
    }

}
