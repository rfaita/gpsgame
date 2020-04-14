package com.game.gps.player.manager.model;

import com.game.gps.player.manager.dto.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String id;
    private String name;
    private Position position;
}
