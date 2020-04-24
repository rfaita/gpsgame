package com.game.dto;

import com.game.model.EventGenerated;
import com.game.model.SituationGenerated;

public class EventGeneratedMessage {

    private String id;
    private EventGenerated.Place place;
    private SituationGenerated firstSituation;
}
