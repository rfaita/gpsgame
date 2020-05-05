package com.game.model;

import com.game.model.minigame.MiniGameState;
import com.game.model.type.PlaceType;
import com.game.model.type.SituationType;
import com.game.util.RandomUtil;
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
public class EventGenerated {

    @Id
    private String id;
    private Place place;
    private Situation firstSituation;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Place {
        private String id;
        private String description;
        private Integer size;
        private PlaceType type;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Situation {
        private String id;
        private Integer minCreatures;
        private Integer maxCreatures;
        private SituationType type;

        public MiniGameState.Situation toMiniGameStateSituation() {
            return MiniGameState.Situation.builder()
                    .id(this.getId())
                    .numberOfCreatures(RandomUtil.random(this.getMinCreatures(), this.getMaxCreatures()))
                    .build();
        }
    }
}
