package com.game.service;

import com.game.model.minigame.MiniGameDataCache;
import com.game.repository.ActionRepository;
import com.game.repository.CreatureRepository;
import com.game.repository.RoomRepository;
import com.game.repository.SituationRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class MiniGameDataCacheService {

    public static final String MINI_GAME_DATA_CACHE_NAME = "miniGameDataCache";

    private final ActionRepository actionRepository;
    private final SituationRepository situationRepository;
    private final RoomRepository roomRepository;
    private final CreatureRepository creatureRepository;

    private final CacheManager cacheManager;

    public Mono<MiniGameDataCache> load() {

        return Mono.justOrEmpty(cacheManager.getCache(MINI_GAME_DATA_CACHE_NAME).get("all", MiniGameDataCache.class))
                .log(MiniGameDataCacheService.class.getName())
                .cast(MiniGameDataCache.class)
                .switchIfEmpty(
                        Mono.just(MiniGameDataCache.builder())
                                .zipWith(this.roomRepository.findAll().collectList(),
                                        (miniGameDataCacheBuilder, rooms) -> miniGameDataCacheBuilder.allRooms(rooms))
                                .zipWith(this.situationRepository.findAll().collectList(),
                                        (miniGameDataCacheBuilder, situations) -> miniGameDataCacheBuilder.allSituations(situations))
                                .zipWith(this.actionRepository.findAll().collectList(),
                                        (miniGameDataCacheBuilder, actions) -> miniGameDataCacheBuilder.allActions(actions))
                                .zipWith(this.creatureRepository.findAll().collectList(),
                                        (miniGameDataCacheBuilder, creatures) -> miniGameDataCacheBuilder.allCreatures(creatures))
                                .map(miniGameDataCacheBuilder -> miniGameDataCacheBuilder.build())
                                .map(miniGameDataCache -> {
                                    cacheManager.getCache(MINI_GAME_DATA_CACHE_NAME).put("all", miniGameDataCache);
                                    return miniGameDataCache;
                                })
                );
    }
}
