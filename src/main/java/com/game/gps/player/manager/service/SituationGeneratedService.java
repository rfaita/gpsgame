package com.game.gps.player.manager.service;

import com.game.gps.player.manager.model.SituationGenerated;
import com.game.gps.player.manager.repository.SituationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SituationGeneratedService {

    private final SituationRepository situationRepository;

    public Mono<SituationGenerated> generate(String id) {

        return this.situationRepository.findById(id)
                .map(situation ->
                        SituationGenerated.builder()
                        .id(situation.getId())
                        .type(situation.getType())
                        .creatures(List.of())//generate n creatures)
                        .survivors(List.of())//generate n survivors)
                        .build());

    }
}
