package com.game.model;

import com.game.model.type.SituationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Situation {

    @Id
    private String id;
    private SituationType type;
    private List<String> usedInPlaces;
    private List<String> usedInRooms;
    private List<String> usedInSituations;
    private Integer minCreatures;
    private Integer maxCreatures;

    public EventGenerated.Situation toEventGeneratedSituation() {
        return EventGenerated.Situation.builder()
                .id(this.getId())
                .minCreatures(this.getMinCreatures())
                .maxCreatures(this.getMaxCreatures())
                .type(this.getType())
                .build();

    }

}
