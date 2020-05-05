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
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Room implements HasRarity {

    @Id
    private String id;
    private Integer maxItems;
    private List<String> usedInPlaces;
    private Rarity rarity;

    public MiniGameState.Room toMiniGameStateRoom() {
        return MiniGameState.Room.builder()
                .id(this.getId())
                .build();
    }

}
