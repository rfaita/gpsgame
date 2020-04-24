package com.game.model;

import com.game.model.minigame.MiniGameState;
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
public class Room {

    @Id
    private String id;
    private String name;
    private List<String> usedInPlaces;


    public MiniGameState.Room toMiniGameStateRoom() {
        return MiniGameState.Room.builder()
                .id(this.getId())
                .name(this.getName())
                .build();
    }

}
