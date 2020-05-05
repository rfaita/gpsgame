package com.game.model.minigame.representation;

import com.game.model.EventGenerated;
import com.game.model.minigame.MiniGame;
import com.game.model.minigame.MiniGameDifficult;
import com.game.model.minigame.Stage;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.stream.Collectors;

@Document
@Builder
@Getter
public class MiniGameRepresentation {

    @Id
    private String id;
    private Stage stage;
    private String playerId;
    private EventGenerated eventGenerated;
    private MiniGameDifficult difficult;

    private MiniGameStateRepresentation currentState;

    private List<MiniGameStateRepresentation> stateHistory;

    public MiniGameRepresentation clearStateHistory() {
        this.stateHistory = null;
        return this;
    }

    public MiniGame toMiniGame() {
        return MiniGame.builder()
                .id(this.getId())
                .playerId(this.getPlayerId())
                .stage(this.getStage())
                .eventGenerated(this.getEventGenerated())
                .difficult(this.getDifficult())
                .currentState(this.getCurrentState().toMiniGameState())
                .stateHistory(this.getStateHistory() != null ? this.getStateHistory().stream()
                        .map(MiniGameStateRepresentation::toMiniGameState)
                        .collect(Collectors.toList()) : null)
                .build();
    }


}
