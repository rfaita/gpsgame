package com.game.model.minigame.representation;

import com.game.model.Player;
import com.game.model.action.ActionType;
import com.game.model.minigame.MiniGameState;
import com.game.model.type.ActionResultType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Getter
public class MiniGameStateRepresentation {

    private Player player;
    private Action lastAction;
    private List<ActionResult> lastActionResults;
    private Room currentRoom;
    private Situation currentSituation;
    private List<Action> currentActions;
    private List<Creature> currentCreatures;

    public MiniGameState toMiniGameState() {
        return MiniGameState.builder()
                .player(this.getPlayer())
                .lastAction(this.getLastAction() != null ? this.getLastAction().toAction() : null)
                .lastActionResults(this.getLastActionResults() != null ? this.getLastActionResults().stream()
                        .map(ActionResult::toActionResult)
                        .collect(Collectors.toList()) : null)
                .currentRoom(this.getCurrentRoom() != null ? this.getCurrentRoom().toRoom() : null)
                .currentSituation(this.getCurrentSituation() != null ? this.getCurrentSituation().toSituation() : null)
                .currentActions(this.getCurrentActions() != null ? this.getCurrentActions().stream()
                        .map(Action::toAction)
                        .collect(Collectors.toList()) : null)
                .currentCreatures(this.getCurrentCreatures() != null ? this.getCurrentCreatures().stream()
                        .map(Creature::toCreature)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    @Builder
    @Getter
    public static class Room {
        private String id;

        public MiniGameState.Room toRoom() {
            return MiniGameState.Room.builder()
                    .id(this.getId())
                    .build();
        }

    }

    @Builder
    @Getter
    public static class Situation {
        private String id;
        private Integer numberOfCreatures;

        public MiniGameState.Situation toSituation() {
            return MiniGameState.Situation.builder()
                    .id(this.getId())
                    .numberOfCreatures(this.getNumberOfCreatures())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class Action {
        private String id;
        private ActionType type;

        public MiniGameState.Action toAction() {
            return MiniGameState.Action.builder()
                    .id(this.getId())
                    .type(this.getType())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ActionResult {
        private ActionResultType type;
        private List<String> args;

        public MiniGameState.ActionResult toActionResult() {
            return MiniGameState.ActionResult.builder()
                    .type(this.getType())
                    .args(this.getArgs())
                    .build();
        }
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

        private Integer knockedDown;

        public MiniGameState.Creature toCreature() {
            return MiniGameState.Creature.builder()
                    .id(this.getId())
                    .name(this.getName())
                    .moveSpeed(this.getMoveSpeed())
                    .distance(this.getDistance())
                    .hpArms(this.getHpArms())
                    .hpBody(this.getHpBody())
                    .hpHead(this.getHpHead())
                    .hpLegs(this.getHpLegs())
                    .burning(this.getBurning())
                    .headFractured(this.getHeadFractured())
                    .bodyFractured(this.getBodyFractured())
                    .armsFractured(this.getArmsFractured())
                    .legsFractured(this.getLegsFractured())
                    .knockedDown(this.getKnockedDown())
                    .build();
        }

    }

}
