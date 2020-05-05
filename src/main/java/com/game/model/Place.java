package com.game.model;

import com.game.model.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Place implements HasRarity {

    @Id
    private String id;
    private Integer minSize;
    private Integer maxSize;
    private PlaceType type;
    private Rarity rarity;

    public EventGenerated.Place toEventGeneratedPlace() {
        return EventGenerated.Place.builder()
                .id(this.getId())
                .size(this.getMinSize())
                .type(this.getType())
                .build();

    }

}
