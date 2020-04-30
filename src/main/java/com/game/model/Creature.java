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

    private Integer moveSpeed;

    private Integer hpHead;
    private Integer hpBody;
    private Integer hpArms;
    private Integer hpLegs;

    public MiniGameState.Creature toMiniGameStateCreature() {
        return MiniGameState.Creature.builder()
                .id(this.getId())
                .name(this.getName())
                .moveSpeed(this.getMoveSpeed())
                .hpLegs(this.getHpLegs())
                .hpBody(this.getHpBody())
                .hpArms(this.getHpArms())
                .hpHead(this.getHpHead())
                .burning(Boolean.FALSE)
                .armsFractured(Boolean.FALSE)
                .headFractured(Boolean.FALSE)
                .bodyFractured(Boolean.FALSE)
                .legsFractured(Boolean.FALSE)
                .distance(3)
                .knockedDown(0)
                .build();
    }

}
