package com.game.model.minigame;

import com.game.model.Player;
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

    @Builder
    @Getter
    public static class Creature {
        private String id;
        private String name;
        private Integer moveSpeed;
        private Integer distance;

        private Integer hpHead;
        private Integer hpBody;
        private Integer hpArms;
        private Integer hpLegs;

        private Boolean burning;
        private Boolean headFractured;
        private Boolean bodyFractured;
        private Boolean armsFractured;
        private Boolean legsFractured;

        private Boolean knockDown;

        public Creature move() {
            if (this.distance - this.moveSpeed <= 0) {
                this.distance = 0;
            } else {
                this.distance -= this.moveSpeed;
            }
            return this;
        }


    }

    @Builder
    @Getter
    public static class Survivor {
        private String id;
        private String name;
        private Integer moveSpeed;
        private Integer distance;

        private Integer hpHead;
        private Integer hpBody;
        private Integer hpArms;
        private Integer hpLegs;

        private Boolean burning;
        private Boolean headFractured;
        private Boolean bodyFractured;
        private Boolean armsFractured;
        private Boolean legsFractured;
    }

}
