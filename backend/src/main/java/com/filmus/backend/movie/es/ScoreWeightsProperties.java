// File: com/filmus/backend/movie/es/ScoreWeightsProperties.java
package com.filmus.backend.movie.es;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "score.weights")
@Getter @Setter
public class ScoreWeightsProperties {
    private float poster = 1.5f;
    private float still = 0.8f;
    private float runtime = 0.5f;
    private float longPlot = 1.0f;
    private float awards1 = 1.2f;
    private float awards2 = 1.1f;
    private float actorRich = 0.7f;
    private float director = 0.5f;
    private float prodYearFactor = 0.01f;
}