package com.game.gps.player.manager.model;

import com.game.gps.player.manager.model.type.SituationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class SituationGenerated {

    @Id
    private String id;
    private String description;
    private SituationType type;
    private List<Survivor> survivors;
    private List<Creature> creatures;
}
