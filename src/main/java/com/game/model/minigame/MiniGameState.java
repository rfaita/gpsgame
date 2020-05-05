package com.game.model.minigame;

import com.game.model.Player;
import com.game.model.action.ActionType;
import com.game.model.minigame.representation.MiniGameStateRepresentation;
import com.game.model.type.ActionResultType;
import com.game.util.ListUtil;
import com.game.util.RandomUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Getter
public class MiniGameState {

    private Player player;
    private Action lastAction;
    private List<ActionResult> lastActionResults;
    private Room currentRoom;
    private Situation currentSituation;
    private List<Action> currentActions;
    private List<Creature> currentCreatures;

    public MiniGameStateRepresentation toMiniGameStateRepresentation() {
        return MiniGameStateRepresentation.builder()
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

    public MiniGameState merge(MiniGameState miniGameState) {
        this.player = miniGameState.getPlayer();
        this.lastAction = miniGameState.getLastAction();
        this.lastActionResults = ListUtil.concat(this.lastActionResults, miniGameState.getLastActionResults());
        this.currentRoom = miniGameState.getCurrentRoom();
        this.currentSituation = miniGameState.getCurrentSituation();
        this.currentActions = miniGameState.getCurrentActions();
        this.currentCreatures = miniGameState.getCurrentCreatures();

        return this;
    }

    public interface Observer {
        void handleActionResult(ActionResult actionResult);
    }

    @Builder
    @Getter
    public static class Room {
        private String id;

        public MiniGameStateRepresentation.Room toRoom() {
            return MiniGameStateRepresentation.Room.builder()
                    .id(this.getId())
                    .build();
        }

    }

    @Builder
    @Getter
    public static class Situation {
        private String id;
        private Integer numberOfCreatures;

        public MiniGameStateRepresentation.Situation toSituation() {
            return MiniGameStateRepresentation.Situation.builder()
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

        public MiniGameStateRepresentation.Action toAction() {
            return MiniGameStateRepresentation.Action.builder()
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

        public MiniGameStateRepresentation.ActionResult toActionResult() {
            return MiniGameStateRepresentation.ActionResult.builder()
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

        private List<Observer> observers;

        public void addObserver(Observer observer) {
            if (this.observers == null) {
                this.observers = new ArrayList<>(1);
            }
            this.observers.add(observer);
        }

        private void notifyActionResult(final ActionResult actionResult) {
            if (this.observers != null) {
                this.observers.stream().forEach(observer -> observer.handleActionResult(actionResult));
            }
        }

        public Creature doAction(Player player) {
            if (this.distance == 0) {
                attackIfPossible(player);
            } else {
                move();
            }

            return this;
        }

        private Creature move() {
            if (this.distance - this.moveSpeed <= 0) {
                this.distance = 0;

                this.notifyActionResult(ActionResult.builder()
                        .type(ActionResultType.ENEMY_CLOSE)
                        .args(List.of(this.name))
                        .build());
            } else {
                this.distance -= this.moveSpeed;
            }

            return this;
        }

        private Creature attackIfPossible(Player player) {
            if (this.knockedDown > 0) {
                this.knockedDown--;
                if (this.knockedDown == 0) {
                    this.notifyActionResult(ActionResult.builder()
                            .type(ActionResultType.ENEMY_WAKE_UP)
                            .args(List.of(this.name))
                            .build());
                }
            }

            if (this.distance == 0 && this.knockedDown == 0) {
                if (RandomUtil.randomPercentage() > 50) {
                    //do dmg to player
                    Integer dmg = RandomUtil.random(1, 10);

                    player.damage(dmg);

                    this.notifyActionResult(ActionResult.builder()
                            .type(ActionResultType.ENEMY_ATTACK_PLAYER)
                            .args(List.of(this.name, String.valueOf(dmg)))
                            .build());
                } else {
                    this.notifyActionResult(ActionResult.builder()
                            .type(ActionResultType.ENEMY_MISS_ATTACK)
                            .args(List.of(this.name))
                            .build());
                }
            }

            return this;
        }

        public Creature knockedDown() {
            if (this.distance == 0 && this.knockedDown == 0) {
                this.knockedDown = 2;//the current turn and the next one

                this.notifyActionResult(ActionResult.builder()
                        .type(ActionResultType.ENEMY_KNOCKED_DOWN)
                        .args(List.of(this.name))
                        .build());
            }
            return this;
        }

        public MiniGameStateRepresentation.Creature toCreature() {
            return MiniGameStateRepresentation.Creature.builder()
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
