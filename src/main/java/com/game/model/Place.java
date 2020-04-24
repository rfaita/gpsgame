package com.game.model;

import com.game.model.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Place {

    @Id
    private String id;
    private String name;
    private Integer size;
    private PlaceType type;

    public EventGenerated.Place toEventGeneratedPlace() {
        return EventGenerated.Place.builder()
                .id(this.getId())
                .name(this.getName())
                .size(this.getSize())
                .type(this.getType())
                .build();

    }

}
