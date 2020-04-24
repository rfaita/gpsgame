package com.game.model.minigame;

import com.game.model.Creature;
import com.game.model.Player;
import com.game.model.Survivor;
import com.game.model.action.ActionType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class MiniGameState {

    private Player player;
    private Action lastAction;
    private Room currentRoom;
    private Situation currentSituation;
    private List<Action> currentActions;
    private List<Creature> currentCreatures;
    private List<Survivor> currentSurvivors;


    @Builder
    @Getter
    public static class Room {
        private String id;
        private String name;
    }

    @Builder
    @Getter
    public static class Situation {
        private String id;
    }

    @Builder
    @Getter
    public static class Action {
        private String id;
        private ActionType type;
    }
}
