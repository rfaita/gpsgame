package com.game.gps.player.manager.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerPositionMessage extends Message {

    private double lat;
    private double lon;

    @Builder
    public PlayerPositionMessage(String playerId, MessageType type, double lat, double lon) {
        super(playerId, type);
        this.lat = lat;
        this.lon = lon;
    }
}
