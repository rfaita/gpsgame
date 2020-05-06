package com.game.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Configuration
@ConfigurationProperties(prefix = "game")
@Getter
@Setter
public class GameProperties {

    @Min(0)
    @Max(100)
    private Double eventPercentageGenerator = 20d;

    private Integer radiusToIteractWithEvent = 500;

    private Integer maxEventsInArea = 2;

    private Integer minEventDuration = 60000;

    private Integer maxEventDuration = 120000;
}
