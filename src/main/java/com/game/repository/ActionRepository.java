package com.game.repository;

import com.game.model.Action;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ActionRepository extends ReactiveMongoRepository<Action, String> {

    @Query("{ usedInSituations: { $all: ['?0'] } }")
    Flux<Action> findAllBySituationId(String situationId);

}
