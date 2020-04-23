package com.game.gps.player.manager.model;

import com.game.gps.player.manager.model.action.ActionType;
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
public class Action {

    @Id
    private String id;
    private ActionType type;
    private List<String> usedInSituations;

    private List<String> possibleRoomsId;
    private List<String> possibleSituationsId;

}
