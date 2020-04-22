package com.game.gps.player.manager.model.minigame;

import com.game.gps.player.manager.model.Action;
import com.game.gps.player.manager.model.Room;
import com.game.gps.player.manager.model.Situation;
import com.game.gps.player.manager.repository.ActionRepository;
import com.game.gps.player.manager.repository.MiniGameRepository;
import com.game.gps.player.manager.repository.RoomRepository;
import com.game.gps.player.manager.repository.SituationRepository;
import com.game.gps.player.manager.service.EventGeneratedService;
import com.game.gps.player.manager.service.PlayerService;
import com.game.gps.player.manager.service.SituationGeneratedService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MiniGameOrchestrator {

    private final EventGeneratedService eventGeneratedService;
    private final SituationGeneratedService situationGeneratedService;

    private final ActionRepository actionRepository;
    private final SituationRepository situationRepository;
    private final RoomRepository roomRepository;

    private final PlayerService playerService;

    private final MiniGameRepository miniGameRepository;

    public Mono<MiniGame> start(String playerId, String eventGeneratedId) {

        return Mono.just(new MiniGame().id(UUID.randomUUID().toString()))
                .flatMap(miniGame -> this.playerService.findById(playerId)
                        .map(player -> miniGame.player(player)))
                .flatMap(miniGame -> this.eventGeneratedService.findById(eventGeneratedId)
                        .map(eventGenerated -> miniGame.eventGenerated(eventGenerated)))
                .flatMap(miniGame -> this.situationGeneratedService.generate(miniGame.currentSituation().getId())
                        .map(firstSituationGenerated -> miniGame.currentSituation(firstSituationGenerated)))
                .flatMap(miniGame -> this.loadActions(miniGame))
                .flatMap(this::saveState);

    }

    public Mono<MiniGame> loadActions(MiniGame miniGame) {
        return this.actionRepository.findAllBySituationId(miniGame.firstSituation().getId())
                .filterWhen(action -> action.getActionType().runAllVerifiers(miniGame))
                .reduce(new ArrayList<Action>(), (list, action) -> {
                    list.add(action);
                    return list;
                })
                .map(list -> miniGame.currentActions(list));
    }

    public void executeAction(String miniGameId, String actionId) {
        this.loadState(miniGameId)
                .flatMap(miniGame -> miniGame.executeAction(actionId))
                .flatMap(this::saveState);
    }

    public void finish(String miniGameId) {
        //this.playerService.save()
    }

    private Mono<MiniGame> loadState(String miniGameId) {
        return this.miniGameRepository.findById(miniGameId)
                .flatMap(miniGame -> this.roomRepository.findAll()
                        .reduce(new ArrayList<Room>(), (list, room) -> {
                            list.add(room);
                            return list;
                        })
                        .map(list -> miniGame.allRooms(list)))
                .flatMap(miniGame -> this.situationRepository.findAll()
                        .reduce(new ArrayList<Situation>(), (list, situation) -> {
                            list.add(situation);
                            return list;
                        })
                        .map(list -> miniGame.allSituations(list)))
                .flatMap(miniGame -> this.actionRepository.findAll()
                        .reduce(new ArrayList<Action>(), (list, action) -> {
                            list.add(action);
                            return list;
                        })
                        .map(list -> miniGame.allActions(list)));
    }


    private Mono<MiniGame> saveState(MiniGame miniGame) {
        return this.miniGameRepository.save(miniGame);
    }

}
