package com.game.model;

import com.game.model.minigame.MiniGameDifficult;
import com.game.model.minigame.MiniGameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Creature {

    @Id
    private String id;
    private String name;
    private MiniGameDifficult difficult;

    public MiniGameState.Creature toMiniGameStateCreature() {
        return MiniGameState.Creature.builder()
                .id(this.getId())
                .name(this.getName())
                .build();
    }

}
