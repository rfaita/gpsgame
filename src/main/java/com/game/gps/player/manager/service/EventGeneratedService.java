package com.game.gps.player.manager.service;

import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.model.type.SituationType;
import com.game.gps.player.manager.repository.EventGeneratedRepository;
import com.game.gps.player.manager.repository.EventRepository;
import com.game.gps.player.manager.repository.PlaceRepository;
import com.game.gps.player.manager.repository.SituationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class EventGeneratedService {

    private final EventRepository eventRepository;
    private final EventGeneratedRepository eventGeneratedRepository;
    private final PlaceRepository placeRepository;
    private final SituationRepository situationRepository;

    public Mono<EventGenerated> visit(final String id) {
        //if the player is not close enough to the event, ignore this action

        return this.eventRepository.existsById(id)
                .filter(Boolean::booleanValue)
                .flatMap(canDo ->
                        this.eventGeneratedRepository.findById(id)
                                .switchIfEmpty(this.generate(id)));
    }

    public Mono<EventGenerated> findById(String id) {
        return this.eventGeneratedRepository.findById(id);
    }

    private Mono<EventGenerated> generate(final String id) {

        return placeRepository.findByRandom()
                .flatMap(place ->
                        situationRepository.findByUsedInPlacesAndTypeAndRandom(place.getId(), SituationType.FIRST)
                                .map(situation ->
                                        EventGenerated.builder()
                                                .id(id)
                                                .place(place.toEventGeneratedPlace())
                                                .firstSituation(situation.toEventGeneratedSituation())
                                                .build()))
                .flatMap(eventGenerated -> eventGeneratedRepository.save(eventGenerated));


    }
}
