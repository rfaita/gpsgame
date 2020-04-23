package com.game.gps.player.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Configuration
@ConfigurationProperties(prefix = "game.event")
@Getter
@Setter
public class EventProperties {

    @Min(0)
    @Max(100)
    private Double eventPercentageGenerator = 100d;

    private Integer radiusToFindEvents = 500;

    private Integer maxEventsInArea = 2;

    private Integer minEventDuration = 600000;

    private Integer maxEventDuration = 1200000;
}
