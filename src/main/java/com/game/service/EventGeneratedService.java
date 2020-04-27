package com.game.service;

import com.game.exception.GenericException;
import com.game.model.EventGenerated;
import com.game.model.type.SituationType;
import com.game.repository.EventGeneratedRepository;
import com.game.repository.EventRepository;
import com.game.repository.PlaceRepository;
import com.game.repository.SituationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.game.exception.ExceptionMessageConstant.RANDOM_PLACE_NOT_FOUND;
import static com.game.exception.ExceptionMessageConstant.RANDOM_SITUATION_NOT_FOUND;

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
                .switchIfEmpty(Mono.error(GenericException.of(RANDOM_PLACE_NOT_FOUND)))
                .flatMap(place ->
                        situationRepository.findByUsedInPlacesAndTypeAndRandom(place.getId(), SituationType.FIRST)
                                .switchIfEmpty(Mono.error(GenericException.of(RANDOM_SITUATION_NOT_FOUND, place.getId())))
                                .map(situation ->
                                        EventGenerated.builder()
                                                .id(id)
                                                .place(place.toEventGeneratedPlace())
                                                .firstSituation(situation.toEventGeneratedSituation())
                                                .build()))
                .flatMap(eventGenerated -> eventGeneratedRepository.save(eventGenerated));


    }
}
