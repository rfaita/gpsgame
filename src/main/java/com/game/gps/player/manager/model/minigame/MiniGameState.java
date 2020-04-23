package com.game.gps.player.manager.model.minigame;

import com.game.gps.player.manager.model.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class MiniGameState {

    private Player player;
    private Room currentRoom;
    private Situation currentSituation;
    private Action currentAction;
    private List<Action> currentActions;
    private List<Creature> currentCreatures;
    private List<Survivor> currentSurvivors;

}
