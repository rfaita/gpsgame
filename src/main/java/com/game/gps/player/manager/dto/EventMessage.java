package com.game.gps.player.manager.dto;

import com.game.gps.player.manager.model.Event;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventMessage extends Message {
    private Event event;

    @Builder
    public EventMessage(String playerId, MessageType type, Event event) {
        super(playerId, type);
        this.event = event;
    }
}
