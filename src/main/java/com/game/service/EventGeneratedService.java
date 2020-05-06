package com.game.service;

import com.game.exception.GenericException;
import com.game.model.Event;
import com.game.model.EventGenerated;
import com.game.model.type.SituationType;
import com.game.repository.EventGeneratedRepository;
import com.game.repository.EventRepository;
import com.game.repository.PlaceRepository;
import com.game.repository.SituationRepository;
import com.game.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.game.exception.ExceptionMessageConstant.*;

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

        return this.eventRepository.findById(id)
                .switchIfEmpty(Mono.error(GenericException.of(EVENT_NOT_FOUND, id)))
                .flatMap(event ->
                        this.eventGeneratedRepository.findById(id)
                                .switchIfEmpty(this.generate(event)));

    }

    public Mono<EventGenerated> findById(String id) {
        return this.eventGeneratedRepository.findById(id);
    }

    private Mono<EventGenerated> generate(final Event event) {

        final Double randomRarity = RandomUtil.random(1d);

        return placeRepository.findByRarity_MinRarityLessThanEqualAndRarity_MaxRarityGreaterThan(randomRarity, randomRarity)
                .switchIfEmpty(Mono.error(GenericException.of(RANDOM_PLACE_NOT_FOUND)))
                .flatMap(place ->
                        situationRepository.findByUsedInPlacesAndTypeAndRandom(place.getId(), SituationType.FIRST)
                                .switchIfEmpty(Mono.error(GenericException.of(RANDOM_SITUATION_NOT_FOUND, place.getId())))
                                .map(situation ->
                                        EventGenerated.builder()
                                                .id(event.getId())
                                                .position(event.getPosition())
                                                .place(place.toEventGeneratedPlace())
                                                .firstSituation(situation.toEventGeneratedSituation())
                                                .build()))
                .flatMap(eventGenerated -> eventGeneratedRepository.save(eventGenerated));


    }
}
