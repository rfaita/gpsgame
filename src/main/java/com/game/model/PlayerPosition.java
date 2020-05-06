package com.game.model;

import com.game.dto.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerPosition {

    private String id;
    private double lat;
    private double lon;

    public Position toPosition() {
        return Position.builder()
                .lon(this.getLon())
                .lat(this.getLat())
                .build();
    }


}
