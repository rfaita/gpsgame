package com.game.model;

import com.game.model.type.SituationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SituationGenerated {

    private String id;
    private String description;
    private SituationType type;

}
