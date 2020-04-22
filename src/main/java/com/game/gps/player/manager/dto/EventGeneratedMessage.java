package com.game.gps.player.manager.dto;

import com.game.gps.player.manager.model.EventGenerated;
import com.game.gps.player.manager.model.SituationGenerated;

public class EventGeneratedMessage  {

    private String id;
    private EventGenerated.Place place;
    private SituationGenerated firstSituation;
}
